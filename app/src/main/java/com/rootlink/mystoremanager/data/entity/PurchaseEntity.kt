package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.PaymentStatus

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["supplierId"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("supplierId")]
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val purchaseId: Long = 0,

    val supplierId: Long,
    val purchaseDate: Long,

    val totalAmount: Double,
    val paidAmount: Double,
    val dueAmount: Double,

    val status: PaymentStatus
)
