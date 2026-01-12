package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StockEntity(
    @PrimaryKey val productId: Long,
    val quantityOnHand: Double,
    val lastUpdated: Long
)
