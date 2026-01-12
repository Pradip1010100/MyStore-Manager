package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.entity.enums.SalaryType
import com.rootlink.mystoremanager.data.entity.enums.WorkerStatus

@Entity(
    tableName = "workers",
    indices = [Index("phone")]
)
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true) val workerId: Long = 0,
    val name: String,
    val phone: String,
    val salaryType: SalaryType,
    val salaryAmount: Double,
    val status: WorkerStatus,
    val createdAt: Long
)
