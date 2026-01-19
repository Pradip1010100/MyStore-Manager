package com.rootlink.mystoremanager.ui.screen.model

data class SupplierLedgerUiItem(
    val date: String,
    val reference: String,
    val label: String,      // "Purchase" | "Payment"
    val amount: Double,     // +purchase, -payment
    val dueAfter: Double
)
