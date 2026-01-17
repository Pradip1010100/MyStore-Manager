package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.TransactionCategory
import com.rootlink.mystoremanager.data.enums.TransactionType

data class TransactionUiItem(
    val transactionId: Long,
    val date: Long,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val paymentMode: PaymentMode,

    val title: String,      // "Salary Payment"
    val subtitle: String?,  // "Worker: Ramesh Kumar"
    val notes: String?
)
