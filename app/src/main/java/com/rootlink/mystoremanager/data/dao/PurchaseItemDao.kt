package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.PurchaseItemEntity

@Dao
interface PurchaseItemDao {

    @Insert
    suspend fun insertAll(items: List<PurchaseItemEntity>)

    @Query("SELECT * FROM purchase_items WHERE purchaseId = :purchaseId")
    suspend fun getByPurchaseId(purchaseId: Long): List<PurchaseItemEntity>
}
