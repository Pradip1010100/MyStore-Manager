package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.SupplierPaymentEntity

@Dao
interface SupplierPaymentDao {

    @Insert
    suspend fun insert(payment: SupplierPaymentEntity): Long

    @Query("""
        SELECT * FROM supplier_payments
        WHERE supplierId = :supplierId
        ORDER BY paymentDate
    """)
    suspend fun getBySupplier(supplierId: Long): List<SupplierPaymentEntity>

    @Query("""
    SELECT COALESCE(SUM(amount), 0)
    FROM supplier_payments
    WHERE supplierId = :supplierId
""")
    suspend fun getTotalPaidBySupplier(supplierId: Long): Double



    @Query("SELECT COALESCE(SUM(totalAmount), 0) FROM purchases WHERE supplierId = :supplierId")
    suspend fun getTotalAmountBySupplier(supplierId: Long): Double

}