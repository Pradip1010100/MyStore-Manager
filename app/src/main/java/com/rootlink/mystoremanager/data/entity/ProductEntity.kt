package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.enums.ProductStatus

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ProductCategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId")]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    val name: String,
    val categoryId: Long,
    val brand: String,
    val unit: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val warrantyMonths: Int,
    val status: ProductStatus
)
