package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_profile")
data class CompanyProfileEntity(
    @PrimaryKey val id: Int = 1,   // always 1 row
    val name: String,
    val businessType: String,
    val address: String,
    val phone: String
)
