package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.entity.enums.PaymentStatus

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val saleId: Long = 0,
    val saleDate: Long,
    val customerId: Long?,
    val totalAmount: Double,
    val discount: Double,
    val finalAmount: Double,
    val paymentStatus: PaymentStatus
)
