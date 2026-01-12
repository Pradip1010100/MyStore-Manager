package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [Index("phone")]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long = 0,

    val name: String,
    val phone: String,
    val address: String?
)
