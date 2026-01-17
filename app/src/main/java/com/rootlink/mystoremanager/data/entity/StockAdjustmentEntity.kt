package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType

@Entity(
    tableName = "stock_adjustments",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("productId")]
)
data class StockAdjustmentEntity(
    @PrimaryKey(autoGenerate = true)
    val adjustmentId: Long = 0,

    val productId: Long,

    val adjustmentType: StockAdjustmentType, // IN / OUT
    val quantity:Int,

    val reason: String,
    val adjustmentDate: Long
)
