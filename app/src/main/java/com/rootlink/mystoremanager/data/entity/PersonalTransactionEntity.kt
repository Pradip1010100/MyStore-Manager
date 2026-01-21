package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.PaymentMode
import com.rootlink.mystoremanager.data.enums.TransactionType

@Entity(tableName = "personal_transactions")
data class PersonalTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val amount: Double,
    val title: String,              // Rent, Medical, Loan, Gift
    val personName: String?,        // landlord / hospital / friend
    val note: String?,
    val direction: TransactionType, // IN or OUT
    val paymentMode: PaymentMode,
    val date: Long
)
