package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.*
import com.rootlink.mystoremanager.data.enums.*
import javax.inject.Inject

class SalesRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val stockDao: StockDao,
    private val customerDao: CustomerDao,
    private val stockAdjustmentDao: StockAdjustmentDao,
    private val oldBatteryDao: OldBatteryDao,
    private val transactionDao: TransactionDao
) {

    suspend fun getAllSales() = saleDao.getAll()
    suspend fun getSaleById(id: Long) = saleDao.getById(id)
    suspend fun getSaleItems(saleId: Long) = saleItemDao.getBySale(saleId)
    suspend fun insertCustomer(customer: CustomerEntity):Long =
        customerDao.insert(customer)
    suspend fun getCustomerById(id: Long): CustomerEntity =
        customerDao.getById(id)
    suspend fun getAllCustomers(): List<CustomerEntity> = customerDao.getAll()
    suspend fun getOldBatteryBySaleId(saleId: Long): OldBatteryEntity? =
        oldBatteryDao.getBySaleId(saleId)

    @Transaction
    suspend fun createSale(
        sale: SaleEntity,
        items: List<SaleItemEntity>,
        oldBattery: OldBatteryEntity?
    ) {
        // 1. STOCK VALIDATION
        items.forEach {
            val stock = stockDao.getStock(it.productId)
                ?: throw IllegalStateException("Stock missing")

            if (stock.quantityOnHand < it.quantity) {
                throw IllegalArgumentException("Insufficient stock")
            }
        }

        // 2. SAVE SALE
        val saleId = saleDao.insert(sale)

        // 3. SAVE ITEMS
        saleItemDao.insertAll(
            items.map { it.copy(saleId = saleId) }
        )

        // 4. UPDATE STOCK + ADJUSTMENT
        items.forEach {
            val stock = stockDao.getStock(it.productId)!!

            stockDao.setStock(
                productId = it.productId,
                newQty = stock.quantityOnHand - it.quantity,
                time = System.currentTimeMillis()
            )

            stockAdjustmentDao.insert(
                StockAdjustmentEntity(
                    productId = it.productId,
                    adjustmentType = StockAdjustmentType.OUT,
                    quantity = it.quantity.toDouble(),
                    reason = "Sale #$saleId",
                    adjustmentDate = System.currentTimeMillis()
                )
            )
        }

        // 5. OLD BATTERY
        oldBattery?.let {
            oldBatteryDao.insert(it.copy(saleId = saleId))
        }

        // 6. TRANSACTION
        transactionDao.insert(
            TransactionEntity(
                transactionDate = System.currentTimeMillis(),
                transactionType = TransactionType.IN,
                category = TransactionCategory.SALE,
                amount = sale.finalAmount,
                paymentMode = PaymentMode.CASH,
                referenceType = TransactionReferenceType.SALE,
                referenceId = saleId,
                notes = "Sale #$saleId"
            )
        )
    }

}
