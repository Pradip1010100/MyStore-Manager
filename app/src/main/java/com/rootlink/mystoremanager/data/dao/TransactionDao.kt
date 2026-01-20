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

    @Query("""
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE transactionType = 'IN'
      AND transactionDate BETWEEN :from AND :to
""")
    suspend fun getCashIn(from: Long, to: Long): Double

    @Query("""
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE transactionType = 'OUT'
      AND transactionDate BETWEEN :from AND :to
""")
    suspend fun getCashOut(from: Long, to: Long): Double

}
