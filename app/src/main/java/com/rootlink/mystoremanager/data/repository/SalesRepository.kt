package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.OldBatteryDao
import com.rootlink.mystoremanager.data.dao.SaleDao
import com.rootlink.mystoremanager.data.dao.SaleItemDao
import com.rootlink.mystoremanager.data.dao.StockDao
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.entity.enums.PaymentMode
import com.rootlink.mystoremanager.data.entity.enums.TransactionCategory
import com.rootlink.mystoremanager.data.entity.enums.TransactionType

class SalesRepository(
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val stockDao: StockDao,
    private val oldBatteryDao: OldBatteryDao,
    private val transactionDao: TransactionDao
) {

    @androidx.room.Transaction
    suspend fun createSale(
        sale: SaleEntity,
        items: List<SaleItemEntity>,
        oldBattery: OldBatteryEntity?
    ) {
        val saleId = saleDao.insert(sale)

        saleItemDao.insertAll(
            items.map { it.copy(saleId = saleId) }
        )

        items.forEach {
            stockDao.updateStock(
                it.productId,
                -it.quantity,
                System.currentTimeMillis()
            )
        }

        oldBattery?.let {
            oldBatteryDao.insert(it.copy(saleId = saleId))
        }

        transactionDao.insert(
            TransactionEntity(
                transactionDate = System.currentTimeMillis(),
                transactionType = TransactionType.IN,
                category = TransactionCategory.SALE,
                amount = sale.finalAmount,
                paymentMode = PaymentMode.CASH,
                referenceId = saleId,
                notes = "Sale"
            )
        )
    }
}
