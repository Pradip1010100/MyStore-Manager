package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_items",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["purchaseId"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("purchaseId"), Index("productId")]
)
data class PurchaseItemEntity(
    @PrimaryKey(autoGenerate = true)
    val purchaseItemId: Long = 0,

    val purchaseId: Long,
    val productId: Long,

    val quantity: Double,
    val unitPrice: Double,
    val lineTotal: Double
)
