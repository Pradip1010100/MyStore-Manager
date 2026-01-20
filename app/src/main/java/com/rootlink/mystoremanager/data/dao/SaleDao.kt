package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.SaleEntity

@Dao
interface SaleDao {

    @Insert
    suspend fun insert(sale: SaleEntity): Long

    @Query("SELECT * FROM sales ORDER BY saleDate DESC")
    suspend fun getAll(): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE saleId = :id")
    suspend fun getById(id: Long): SaleEntity

    @Query("""
    SELECT COUNT(*)
    FROM sales
    WHERE saleDate BETWEEN :from AND :to
""")
    suspend fun getSalesCountBetween(
        from: Long,
        to: Long
    ): Int

}
