package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.data.enums.WorkType
import com.rootlink.mystoremanager.data.enums.WorkerStatus

@Entity(tableName = "workers")
data class WorkerEntity(

    @PrimaryKey(autoGenerate = true)
    val workerId: Long = 0,

    /* -------------------------
       BASIC INFO
     ------------------------- */
    val name: String,

    val phone: String? = null,

    val email: String? = null,

    val address: String? = null,

    val profileImageUri: String? = null,

    /* -------------------------
       WORK INFO
     ------------------------- */
    val team: String? = null,

    val position: String? = null,

    /* -------------------------
       SALARY INFO
     ------------------------- */
    val salaryType: SalaryType,
    // DAILY, MONTHLY, PER_JOB

    val defaultRate: Double,
    // rate per day / month / job

    val salaryAmount: Double,
    // can be same as defaultRate or derived later

    /* -------------------------
       STATUS & DATES
     ------------------------- */
    val status: WorkerStatus = WorkerStatus.ACTIVE,

    val dob: Long? = null,

    val joinedAt: Long = System.currentTimeMillis(),

    val leftAt: Long? = null,

    /* -------------------------
       MISC
     ------------------------- */
    val notes: String? = null
)
