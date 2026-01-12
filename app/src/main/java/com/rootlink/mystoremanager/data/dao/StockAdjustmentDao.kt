package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity

@Dao
interface StockAdjustmentDao {

    @Insert
    suspend fun insert(adjustment: StockAdjustmentEntity)

    @Query("SELECT * FROM stock_adjustments WHERE productId = :productId")
    suspend fun getByProduct(productId: Long): List<StockAdjustmentEntity>
}
