package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.OrderItemEntity

@Dao
interface OrderItemDao {

    @Insert
    suspend fun insertAll(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getByOrderId(orderId: Long): List<OrderItemEntity>
}
