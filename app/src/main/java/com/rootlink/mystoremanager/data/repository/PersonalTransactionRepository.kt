package com.rootlink.mystoremanager.data.repository

import com.rootlink.mystoremanager.data.dao.PersonalTransactionDao
import com.rootlink.mystoremanager.data.dao.TransactionDao
import com.rootlink.mystoremanager.data.entity.PersonalTransactionEntity
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.enums.TransactionCategory
import com.rootlink.mystoremanager.data.enums.TransactionReferenceType
import com.rootlink.mystoremanager.data.enums.TransactionType
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PersonalTransactionRepository @Inject constructor(
    private val personalDao: PersonalTransactionDao,
    private val transactionDao: TransactionDao
) {

    suspend fun addPersonalTransaction(tx: PersonalTransactionEntity): Long {

        val id = personalDao.insert(tx)

        transactionDao.insert(
            TransactionEntity(
                transactionDate = tx.date,
                transactionType = tx.direction, // IN / OUT
                category = TransactionCategory.PERSONAL,
                amount = tx.amount,
                paymentMode = tx.paymentMode,
                referenceType = TransactionReferenceType.PERSONAL,
                referenceId = id,
                notes = tx.note
            )
        )

        return id
    }

    suspend fun getAll(): List<PersonalTransactionEntity> {
        return personalDao.getAll()
    }
}
