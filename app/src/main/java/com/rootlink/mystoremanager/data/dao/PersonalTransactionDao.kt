package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.PersonalTransactionEntity

@Dao
interface PersonalTransactionDao {

    @Insert
    suspend fun insert(tx: PersonalTransactionEntity): Long

    @Query("""
        SELECT * FROM personal_transactions
        ORDER BY date DESC
    """)
    suspend fun getAll(): List<PersonalTransactionEntity>

    // ✅ REQUIRED
    @Query("""
        SELECT * FROM personal_transactions
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Long): PersonalTransactionEntity
}
