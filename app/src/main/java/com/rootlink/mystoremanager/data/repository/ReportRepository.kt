package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.PurchaseDao
import com.rootlink.mystoremanager.data.dao.SaleDao
import com.rootlink.mystoremanager.data.dao.StockDao
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.dao.WorkerPaymentDao
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.entity.enums.TransactionCategory

class ReportRepository(
    private val saleDao: SaleDao,
    private val purchaseDao: PurchaseDao,
    private val transactionDao: TransactionDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val stockDao: StockDao
) {

    suspend fun getDailySalesTotal(date: Long): Double {
        return 0.0
//  TODO      return transactionDao.getTotalByCategoryAndDate(
//            TransactionCategory.SALE,
//            date
//        )
    }

    suspend fun getWorkerPayments(
        from: Long,
        to: Long
    ): List<WorkerPaymentEntity> {
        return emptyList()
        //TODO return workerPaymentDao.getBetween(from, to)
    }
}
