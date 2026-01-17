package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.StockEntity

@Dao
interface StockDao {

    @Insert
    suspend fun insert(stock: StockEntity)

    @Query("""
        UPDATE stock 
        SET quantityOnHand = quantityOnHand + :delta,
            lastUpdated = :time
        WHERE productId = :productId
    """)
    suspend fun updateStock(
        productId: Long,
        delta: Int,
        time: Long
    )

    @Query("SELECT * FROM stock WHERE productId = :productId")
    suspend fun getStock(productId: Long): StockEntity
}
