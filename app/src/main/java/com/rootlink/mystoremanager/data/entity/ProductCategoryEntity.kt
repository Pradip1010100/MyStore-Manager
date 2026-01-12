package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.entity.enums.ProductStatus

@Entity(tableName = "product_categories")
data class ProductCategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val name: String,
    val description: String?,
    val status: ProductStatus
)
