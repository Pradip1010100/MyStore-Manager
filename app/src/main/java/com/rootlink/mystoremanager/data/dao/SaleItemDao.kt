package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.SaleItemEntity

@Dao
interface SaleItemDao {

    @Insert
    suspend fun insertAll(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getItems(saleId: Long): List<SaleItemEntity>
}
