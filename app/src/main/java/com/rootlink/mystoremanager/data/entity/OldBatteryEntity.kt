package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "old_batteries",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["saleId"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("saleId")]
)
data class OldBatteryEntity(
    @PrimaryKey(autoGenerate = true) val oldBatteryId: Long = 0,
    val saleId: Long,
    val batteryType: String,
    val quantity: Int,
    val weight: Double,
    val rate: Double,
    val amount: Double
)
