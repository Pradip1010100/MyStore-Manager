package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.*
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val workerDao: WorkerDao,
    private val supplierDao: SupplierDao,
    private val transactionDao: TransactionDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val stockDao: StockDao
) {

    suspend fun getWorkers():List<WorkerEntity>{
        return workerDao.getActive()
    }

    suspend fun getSuppliers():List<SupplierEntity>{
        return supplierDao.getActive()
    }

    suspend fun getTodaySalesAmount(
        from: Long,
        to: Long
    ): Double = transactionDao.getCashIn(from, to)

    suspend fun getTodayPurchaseAmount(
        from: Long,
        to: Long
    ): Double = transactionDao.getCashOut(from, to)

    suspend fun getTodaySalesCount(
        from: Long,
        to: Long
    ): Int = saleDao.getSalesCountBetween(from, to)

    suspend fun getCashIn(
        from: Long,
        to: Long
    ): Double = transactionDao.getCashIn(from, to)

    suspend fun getCashOut(
        from: Long,
        to: Long
    ): Double = transactionDao.getCashOut(from, to)

    suspend fun getWorkersPaidCount(
        from: Long,
        to: Long
    ): Int =
        workerPaymentDao.getPaymentCountBetween(from, to)

    suspend fun getLowStockCount(
        limit: Double
    ): Int = stockDao.getLowStockCount(limit)
}
