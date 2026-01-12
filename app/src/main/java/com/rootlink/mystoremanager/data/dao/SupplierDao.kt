package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.SupplierEntity

@Dao
interface SupplierDao {

    @Insert
    suspend fun insert(supplier: SupplierEntity): Long

    @Update
    suspend fun update(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET status = 'INACTIVE' WHERE supplierId = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT * FROM suppliers WHERE status = 'ACTIVE'")
    suspend fun getActive(): List<SupplierEntity>
}
