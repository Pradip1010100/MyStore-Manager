package com.rootlink.mystoremanager.data.repository

import androidx.room.Transaction
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.dao.WorkerAttendanceDao
import com.rootlink.mystoremanager.data.dao.WorkerDao
import com.rootlink.mystoremanager.data.dao.WorkerPaymentDao
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.entity.WorkerAttendanceEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.PaymentType
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.data.enums.TransactionCategory
import com.rootlink.mystoremanager.data.enums.TransactionReferenceType
import com.rootlink.mystoremanager.data.enums.TransactionType
import com.rootlink.mystoremanager.data.enums.WorkerPaymentStatus
import com.rootlink.mystoremanager.data.enums.WorkerStatus
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    private val workerDao: WorkerDao,
    private val workerAttendanceDao: WorkerAttendanceDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val transactionDao: TransactionDao
) {

    suspend fun getActiveWorkers(): List<WorkerEntity> {
        return workerDao.getActive()
    }

    suspend fun getWorkerBalance(
        workerId: Long,
        monthStart: Long,
        monthEnd: Long
    ): Pair<Double, Double> {
        val worker = workerDao.getById(workerId)
        val salary = calculateSalary(worker, monthStart, monthEnd)
        val paid = workerPaymentDao.getTotalPaid(workerId)
        return salary to paid
    }


    @Transaction
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
                notes = "Worker payment",
                referenceType = TransactionReferenceType.WORKER_PAYMENT
            )
        )
    }

    suspend fun addWorker(worker: WorkerEntity) {
        workerDao.insert(worker)
    }
    

    suspend fun markAttendance(
        attendance: WorkerAttendanceEntity
    ) {
        workerAttendanceDao.markAttendance(attendance)
    }

    suspend fun getAttendanceForWorkerBetween(
        workerId: Long,
        start: Long,
        end: Long
    ): List<WorkerAttendanceEntity> {
        return workerAttendanceDao.getAttendanceForWorkerBetween(
            workerId,
            start,
            end
        )
    }

    suspend fun getAttendanceForDate(
        date: Long
    ): List<WorkerAttendanceEntity> {
        return workerAttendanceDao.getAttendanceForDate(date)
    }

    suspend fun calculateSalary(
        worker: WorkerEntity,
        from: Long,
        to: Long
    ): Double {
        return when (worker.salaryType) {

            SalaryType.DAILY -> {
                val presentDays =
                    workerAttendanceDao.getPresentDays(
                        worker.workerId,
                        from,
                        to
                    )
                presentDays * worker.salaryAmount
            }

            SalaryType.MONTHLY -> {
                worker.salaryAmount
            }

            else -> {0.0}
        }
    }

    @Transaction
    suspend fun generateSalary(
        workerId: Long,
        from: Long,
        to: Long,
        paymentMode: PaymentMode
    ) {
        val worker = workerDao.getById(workerId)
        require(worker.status == WorkerStatus.ACTIVE)

        val salary = calculateSalary(worker, from, to)
        require(salary > 0)

        val paymentId =
            workerPaymentDao.insert(
                WorkerPaymentEntity(
                    paymentId = 0,
                    workerId = workerId,
                    amount = salary,
                    paymentType = PaymentType.SALARY,
                    paymentDate = System.currentTimeMillis(),
                    notes = "Salary generated",
                    status = WorkerPaymentStatus.COMPLETED
                )
            )

        transactionDao.insert(
            TransactionEntity(
                transactionDate = System.currentTimeMillis(),
                transactionType = TransactionType.OUT,
                category = TransactionCategory.SALARY,
                amount = salary,
                paymentMode = paymentMode,
                referenceId = paymentId,
                notes = "Monthly salary",
                referenceType = TransactionReferenceType.WORKER_PAYMENT
            )
        )
    }



    suspend fun getPaymentsForWorker(
        workerId: Long
    ): List<WorkerPaymentEntity> {
        return workerPaymentDao.getByWorker(workerId)
    }

    suspend fun getWorkerById(workerId: Long): WorkerEntity {
        return workerDao.getById(workerId)
    }

    suspend fun deactivateWorker(workerId: Long) {
        workerDao.deactivate(workerId)
    }

}
