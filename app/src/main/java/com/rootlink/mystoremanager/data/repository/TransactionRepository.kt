package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.dao.WorkerDao
import com.rootlink.mystoremanager.data.dao.WorkerPaymentDao
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.enums.TransactionReferenceType
import com.rootlink.mystoremanager.ui.viewmodel.state.TransactionUiItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val workerDao: WorkerDao
) {

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    suspend fun getTransactions(
        from: Long,
        to: Long
    ): List<TransactionEntity> {
        return transactionDao.getByDateRange(from, to)
    }

    suspend fun getTransactionUiItems(
        from: Long,
        to: Long
    ): List<TransactionUiItem> {

        val transactions = transactionDao.getByDateRange(from, to)

        return transactions.map { tx ->

            when (tx.referenceType) {

                TransactionReferenceType.WORKER_PAYMENT -> {
                    val payment =
                        workerPaymentDao.getById(tx.referenceId)

                    val worker =
                        workerDao.getById(payment.workerId)

                    TransactionUiItem(
                        transactionId = tx.transactionId,
                        date = tx.transactionDate,
                        type = tx.transactionType,
                        category = tx.category,
                        amount = tx.amount,
                        paymentMode = tx.paymentMode,
                        title = "Salary Payment",
                        subtitle = "Worker: ${worker.name}",
                        notes = payment.notes ?: tx.notes
                    )
                }

                TransactionReferenceType.SALE -> {
                    TransactionUiItem(
                        transactionId = tx.transactionId,
                        date = tx.transactionDate,
                        type = tx.transactionType,
                        category = tx.category,
                        amount = tx.amount,
                        paymentMode = tx.paymentMode,
                        title = "Sale",
                        subtitle = null,
                        notes = tx.notes
                    )
                }

                TransactionReferenceType.PURCHASE -> {
                    TransactionUiItem(
                        transactionId = tx.transactionId,
                        date = tx.transactionDate,
                        type = tx.transactionType,
                        category = tx.category,
                        amount = tx.amount,
                        paymentMode = tx.paymentMode,
                        title = "Purchase",
                        subtitle = null,
                        notes = tx.notes
                    )
                }

                TransactionReferenceType.EXPENSE -> {
                    TransactionUiItem(
                        transactionId = tx.transactionId,
                        date = tx.transactionDate,
                        type = tx.transactionType,
                        category = tx.category,
                        amount = tx.amount,
                        paymentMode = tx.paymentMode,
                        title = "Expense",
                        subtitle = null,
                        notes = tx.notes
                    )
                }
            }
        }
    }

}
