package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*
import com.rootlink.mystoremanager.data.enums.*
import com.rootlink.mystoremanager.ui.screen.model.PurchaseItemUi
import com.rootlink.mystoremanager.ui.screen.model.SupplierPurchaseUi
import com.rootlink.mystoremanager.ui.screen.model.SupplierTotals
import com.rootlink.mystoremanager.ui.viewmodel.state.SupplierLedgerItem
import com.rootlink.mystoremanager.util.toReadableDateTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SupplierRepository @Inject constructor(
    private val supplierDao: SupplierDao,
    private val purchaseDao: PurchaseDao,
    private val purchaseItemDao: PurchaseItemDao,
    private val supplierPaymentDao: SupplierPaymentDao,
    private val productDao: ProductDao,
    private val stockDao: StockDao,
    private val stockAdjustmentDao: StockAdjustmentDao,
    private val transactionDao: TransactionDao
) {
        /* ---------- SUPPLIERS ---------- */

    suspend fun getActiveSuppliers(): List<SupplierEntity> =
        supplierDao.getActive()

    suspend fun getSupplierById(id: Long): SupplierEntity =
        supplierDao.getById(id)

    suspend fun addSupplier(supplier: SupplierEntity): Long =
        supplierDao.insert(supplier)

    suspend fun deactivateSupplier(id: Long) =
        supplierDao.deactivate(id)

    suspend fun getSuppliersWithDue():
            List<Pair<SupplierEntity, Double>> {

        return supplierDao.getActive().map {
            it to getSupplierDue(it.supplierId)
        }
    }

    /* ---------- PRODUCTS ---------- */

    suspend fun getActiveProducts(): List<ProductEntity> =
        productDao.getActive()

    /* ---------- PURCHASE ---------- */

    @Transaction
    suspend fun recordPurchase(
        supplierId: Long,
        items: List<PurchaseItemUi>,
        paidAmount: Double
    ) {
        require(items.isNotEmpty())

        val total = items.sumOf { it.lineTotal }
        val due = total - paidAmount
        val now = System.currentTimeMillis()

        // 1️⃣ Insert purchase
        val purchaseId = purchaseDao.insert(
            PurchaseEntity(
                supplierId = supplierId,
                purchaseDate = now,
                totalAmount = total,
                paidAmount = paidAmount,
                dueAmount = due,
                status = when {
                    paidAmount == 0.0 -> PaymentStatus.CREATED
                    paidAmount < total -> PaymentStatus.PARTIALLY_PAID
                    else -> PaymentStatus.PAID
                }
            )
        )

        // 2️⃣ Insert purchase items
        purchaseItemDao.insertAll(
            items.map { ui ->
                PurchaseItemEntity(
                    purchaseId = purchaseId,
                    productId = ui.productId,
                    quantity = ui.quantity,
                    unitPrice = ui.price,
                    lineTotal = ui.lineTotal
                )
            }
        )

        // 3️⃣ Update stock + history (SAFE)
        items.forEach { ui ->

            val existingStock =
                stockDao.getStock(ui.productId)
                    ?: StockEntity(
                        productId = ui.productId,
                        quantityOnHand = 0.0,
                        lastUpdated = now
                    ).also {
                        stockDao.insert(it)
                    }

            val newQty = existingStock.quantityOnHand + ui.quantity

            stockDao.setStock(
                productId = ui.productId,
                newQty = newQty,
                time = now
            )

            // 🔹 Audit trail (VERY IMPORTANT)
            stockAdjustmentDao.insert(
                StockAdjustmentEntity(
                    productId = ui.productId,
                    adjustmentType = StockAdjustmentType.IN,
                    quantity = ui.quantity.toDouble(),
                    reason = "Purchase from supplier #$supplierId",
                    adjustmentDate = now
                )
            )
        }


        // 4️⃣ IF MONEY PAID → CREATE SUPPLIER PAYMENT
        if (paidAmount > 0) {

            val paymentId = supplierPaymentDao.insert(
                SupplierPaymentEntity(
                    supplierId = supplierId,
                    paymentDate = now,
                    amount = paidAmount,
                    paymentMode = PaymentMode.CASH, // param later
                    notes = "Payment against Purchase #$purchaseId"
                )
            )

            // 5️⃣ Accounting entry
            transactionDao.insert(
                TransactionEntity(
                    transactionDate = now,
                    transactionType = TransactionType.OUT,
                    category = TransactionCategory.PURCHASE,
                    amount = paidAmount,
                    paymentMode = PaymentMode.CASH,
                    referenceType = TransactionReferenceType.SUPPLIER_PAYMENT,
                    referenceId = paymentId,
                    notes = "Supplier payment"
                )
            )
        }
    }


    /* ---------- PAY SUPPLIER ---------- */

    @Transaction
    suspend fun paySupplier(payment: SupplierPaymentEntity) {
        val id = supplierPaymentDao.insert(payment)

        transactionDao.insert(
            TransactionEntity(
                transactionDate = payment.paymentDate,
                transactionType = TransactionType.OUT,
                category = TransactionCategory.PURCHASE,
                amount = payment.amount,
                paymentMode = payment.paymentMode,
                referenceType = TransactionReferenceType.SUPPLIER_PAYMENT,
                referenceId = id,
                notes = payment.notes
            )
        )
    }

    /* ---------- PURCHASE HISTORY ---------- */
    suspend fun getSupplierTotals(supplierId: Long): SupplierTotals {
        val totalPurchased = purchaseDao.getTotalAmountBySupplier(supplierId)
        val payments = supplierPaymentDao.getTotalPaidBySupplier(supplierId)

        return SupplierTotals(
            totalPurchased = totalPurchased,
            totalPaid = payments,
            due = totalPurchased - payments
        )
    }

    suspend fun getSupplierPurchaseHistory(
        supplierId: Long
    ): List<SupplierPurchaseUi> {
        return purchaseDao.getBySupplier(supplierId).map { p ->
            val items = purchaseItemDao.getByPurchaseId(p.purchaseId)
            SupplierPurchaseUi(
                purchaseId = p.purchaseId,
                date = p.purchaseDate.toReadableDateTime(),
                items = items.map {
                    val product = productDao.getById(it.productId)
                    PurchaseItemUi(
                        productId = it.productId,
                        productName = product.name,
                        quantity = it.quantity,
                        price = it.unitPrice
                    )
                },
                totalAmount = p.totalAmount,
                dueAmount = p.dueAmount
            )
        }
    }

    /* ---------- LEDGER ---------- */

    suspend fun getSupplierLedger(
        supplierId: Long
    ): List<SupplierLedgerItem> {

        val purchases = purchaseDao.getBySupplier(supplierId).map {
            SupplierLedgerItem(
                date = it.purchaseDate,
                type = SupplierLedgerType.PURCHASE,
                referenceId = it.purchaseId,
                debit = it.totalAmount,
                credit = 0.0,
                note = "Purchase"
            )
        }

        val payments = supplierPaymentDao.getBySupplier(supplierId).map {
            SupplierLedgerItem(
                date = it.paymentDate,
                type = SupplierLedgerType.PAYMENT,
                referenceId = it.paymentId,
                debit = 0.0,
                credit = it.amount,
                note = it.notes
            )
        }

        return (purchases + payments).sortedBy { it.date }
    }

    suspend fun updateSupplier(supplier: SupplierEntity) {
        supplierDao.update(supplier)
    }

    suspend fun getSupplierDue(supplierId: Long): Double =
        purchaseDao.getTotalAmountBySupplier(supplierId) -
                supplierPaymentDao.getTotalPaidBySupplier(supplierId)
}
