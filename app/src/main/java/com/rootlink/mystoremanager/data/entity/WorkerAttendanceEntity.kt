package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.AttendanceStatus

@Entity(
    tableName = "worker_attendance",
    indices = [Index("workerId", "date", unique = true)]
)
data class WorkerAttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val attendanceId: Long = 0,
    val workerId: Long,
    val date: Long,               // startOfDay millis
    val status: AttendanceStatus  // PRESENT / ABSENT
)
