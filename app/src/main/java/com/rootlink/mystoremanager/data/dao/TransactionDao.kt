package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.TransactionEntity

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(tx: TransactionEntity)

    @Query("""
        SELECT * FROM transactions
        WHERE transactionDate BETWEEN :from AND :to
        ORDER BY transactionDate
    """)
    suspend fun getByDateRange(
        from: Long,
        to: Long
    ): List<TransactionEntity>
}
