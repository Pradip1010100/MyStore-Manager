package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.enums.AttendanceStatus

data class AttendanceUiItem(
    val workerId: Long,
    val workerName: String,
    var status: AttendanceStatus
)
