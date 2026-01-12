package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*
import com.rootlink.mystoremanager.data.entity.enums.*

class PurchaseRepository(
    private val purchaseDao: PurchaseDao,
    private val purchaseItemDao: PurchaseItemDao,
    private val stockDao: StockDao,
    private val transactionDao: TransactionDao
) {

    @Transaction
    suspend fun recordPurchase(
        purchase: PurchaseEntity,
        items: List<PurchaseItemEntity>,
        paymentMode: PaymentMode
    ) {
        require(items.isNotEmpty()) {
            "Purchase must have at least one item"
        }

        val purchaseId = purchaseDao.insert(purchase)

        // Insert purchase items
        purchaseItemDao.insertAll(
            items.map { it.copy(purchaseId = purchaseId) }
        )

        // Increment stock
        items.forEach { item ->
            stockDao.updateStock(
                productId = item.productId,
                delta = item.quantity,
                time = System.currentTimeMillis()
            )
        }

        // Create transaction ONLY if money is paid
        if (purchase.paidAmount > 0) {
            transactionDao.insert(
                TransactionEntity(
                    transactionDate = System.currentTimeMillis(),
                    transactionType = TransactionType.OUT,
                    category = TransactionCategory.PURCHASE,
                    amount = purchase.paidAmount,
                    paymentMode = paymentMode,
                    referenceId = purchaseId,
                    notes = "Purchase payment"
                )
            )
        }
    }
}
