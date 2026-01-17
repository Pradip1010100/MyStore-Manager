package com.rootlink.mystoremanager.data.entity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.PaymentMode

@Entity(
    tableName = "supplier_payments",
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
data class SupplierPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val paymentId: Long = 0,

    val supplierId: Long,
    val paymentDate: Long,
    val amount: Double,
    val paymentMode: PaymentMode,
    val notes: String?
)