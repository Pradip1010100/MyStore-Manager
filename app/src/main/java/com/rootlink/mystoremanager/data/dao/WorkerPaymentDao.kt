package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity

@Dao
interface WorkerPaymentDao {

    @Insert
    suspend fun insert(payment: WorkerPaymentEntity): Long

    @Update
    suspend fun update(payment: WorkerPaymentEntity)

    @Query("""
    SELECT IFNULL(SUM(amount), 0)
    FROM worker_payments
    WHERE workerId = :workerId
""")
    suspend fun getTotalPaid(workerId: Long): Double

    @Query("""
    SELECT * FROM worker_payments
    WHERE paymentId = :paymentId
    LIMIT 1
""")
    suspend fun getById(paymentId: Long): WorkerPaymentEntity


    @Query("SELECT * FROM worker_payments WHERE workerId = :workerId")
    suspend fun getByWorker(workerId: Long): List<WorkerPaymentEntity>

    @Query("""
    SELECT COUNT(*)
    FROM worker_payments
    WHERE paymentDate BETWEEN :from AND :to
""")
    suspend fun getPaymentCountBetween(
        from: Long,
        to: Long
    ): Int

}
