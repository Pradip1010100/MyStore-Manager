package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rootlink.mystoremanager.data.entity.enums.SupplierStatus

@Entity(
    tableName = "suppliers",
    indices = [
        Index(value = ["phone"], unique = false)
    ]
)
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val supplierId: Long = 0,

    val name: String,
    val phone: String,
    val address: String?,
    val status: SupplierStatus,
    val createdAt: Long
)
