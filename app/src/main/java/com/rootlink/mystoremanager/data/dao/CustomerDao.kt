package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.CustomerEntity

@Dao
interface CustomerDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: CustomerEntity): Long

    // UPDATE
    @Update
    suspend fun update(customer: CustomerEntity)

    // READ
    @Query("SELECT * FROM customers WHERE customerId = :id")
    suspend fun getById(id: Long): CustomerEntity

    @Query("SELECT * FROM customers ORDER BY name")
    suspend fun getAll(): List<CustomerEntity>
}
