package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.entity.enums.PaymentMode
import com.rootlink.mystoremanager.data.entity.enums.TransactionCategory
import com.rootlink.mystoremanager.data.entity.enums.TransactionType

@Entity(
    tableName = "transactions",
    indices = [Index("referenceId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Long = 0,
    val transactionDate: Long,
    val transactionType: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val paymentMode: PaymentMode,
    val referenceId: Long,
    val notes: String?
)
