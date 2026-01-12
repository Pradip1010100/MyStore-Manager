package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.dao.WorkerDao
import com.rootlink.mystoremanager.data.dao.WorkerPaymentDao
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.entity.enums.PaymentMode
import com.rootlink.mystoremanager.data.entity.enums.TransactionCategory
import com.rootlink.mystoremanager.data.entity.enums.TransactionType
import com.rootlink.mystoremanager.data.entity.enums.WorkerStatus

class WorkerRepository(
    private val workerDao: WorkerDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val transactionDao: TransactionDao
) {

    suspend fun getActiveWorkers(): List<WorkerEntity> {
        return workerDao.getActive()
    }

    @androidx.room.Transaction
    suspend fun payWorker(
        payment: WorkerPaymentEntity,
        paymentMode: PaymentMode
    ) {
        val worker = workerDao.getById(payment.workerId)
        require(worker.status == WorkerStatus.ACTIVE) {
            "Inactive worker cannot be paid"
        }

        val paymentId = workerPaymentDao.insert(payment)

        transactionDao.insert(
            TransactionEntity(
                transactionDate = System.currentTimeMillis(),
                transactionType = TransactionType.OUT,
                category = TransactionCategory.SALARY,
                amount = payment.amount,
                paymentMode = paymentMode,
                referenceId = paymentId,
                notes = "Worker payment"
            )
        )
    }
}
