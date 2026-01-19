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

    @Query("SELECT * FROM purchases WHERE supplierId = :supplierId")
    suspend fun getBySupplier(supplierId: Long): List<PurchaseEntity>

    @Query("""
    SELECT COALESCE(SUM(totalAmount), 0)
    FROM purchases
    WHERE supplierId = :supplierId
""")
    suspend fun getTotalAmountBySupplier(supplierId: Long): Double

    @Query("""
    SELECT COALESCE(SUM(paidAmount),0)
    FROM purchases
    WHERE supplierId = :supplierId
""")
    suspend fun getTotalPaidAtPurchaseBySupplier(supplierId: Long): Double

}
