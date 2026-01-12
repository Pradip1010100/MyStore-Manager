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

    @Update
    suspend fun update(sale: SaleEntity)

    @Query("SELECT * FROM sales WHERE saleId = :id")
    suspend fun getById(id: Long): SaleEntity
}
