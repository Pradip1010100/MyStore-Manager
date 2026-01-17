package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.enums.SupplierLedgerType

data class SupplierLedgerItem(
    val date: Long,
    val type: SupplierLedgerType,
    val referenceId: Long,
    val debit: Double,
    val credit: Double,
    val note: String?
)