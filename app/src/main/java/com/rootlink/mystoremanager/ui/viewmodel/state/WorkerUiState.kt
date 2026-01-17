package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.AttendanceStatus

data class WorkerUiState(
    val isLoading: Boolean = false,

    // LIST SCREEN
    val workers: List<WorkerEntity> = emptyList(),

    // PROFILE / DETAIL SCREEN
    val selectedWorker: WorkerEntity? = null,

    // ATTENDANCE
    val attendance: List<AttendanceUiItem> = emptyList(),

    val monthlyAttendance: Map<Long, AttendanceStatus> = emptyMap(),

    // LEDGER
    val payments: List<WorkerPaymentEntity> = emptyList(),

    // SALARY SUMMARY
    val calculatedSalary: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balance: Double = 0.0,

    val error: String? = null
)
