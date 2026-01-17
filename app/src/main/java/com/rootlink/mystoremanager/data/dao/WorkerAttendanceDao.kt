package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.WorkerAttendanceEntity

@Dao
interface WorkerAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markAttendance(attendance: WorkerAttendanceEntity)

    @Query("""
        SELECT * FROM worker_attendance
        WHERE date = :date
    """)
    suspend fun getAttendanceForDate(
        date: Long
    ): List<WorkerAttendanceEntity>

    @Query("""
        SELECT COUNT(*) FROM worker_attendance
        WHERE workerId = :workerId
        AND status = 'PRESENT'
        AND date BETWEEN :from AND :to
    """)
    suspend fun getPresentDays(
        workerId: Long,
        from: Long,
        to: Long
    ): Int

    @Query("""
    SELECT * FROM worker_attendance
    WHERE workerId = :workerId
      AND date BETWEEN :start AND :end
""")
    suspend fun getAttendanceForWorkerBetween(
        workerId: Long,
        start: Long,
        end: Long
    ): List<WorkerAttendanceEntity>

}
