package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.PurchaseEntity

@Dao
interface PurchaseDao {

    @Insert
    suspend fun insert(purchase: PurchaseEntity): Long

    @Update
    suspend fun update(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases WHERE purchaseId = :id")
    suspend fun getById(id: Long): PurchaseEntity?
}
