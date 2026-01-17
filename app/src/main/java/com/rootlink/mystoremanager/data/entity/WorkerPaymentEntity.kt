package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.PaymentType
import com.rootlink.mystoremanager.data.enums.WorkerPaymentStatus

@Entity(
    tableName = "worker_payments",
    foreignKeys = [
        ForeignKey(
            entity = WorkerEntity::class,
            parentColumns = ["workerId"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("workerId")]
)
data class WorkerPaymentEntity(
    @PrimaryKey(autoGenerate = true) val paymentId: Long = 0,
    val workerId: Long,
    val paymentDate: Long,
    val paymentType: PaymentType,
    val amount: Double,
    val status: WorkerPaymentStatus,
    val notes: String?
)
