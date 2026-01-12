package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.OrderEntity

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: OrderEntity): Long

    @Update
    suspend fun update(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderId = :id")
    suspend fun getById(id: Long): OrderEntity?
}
