package com.rootlink.mystoremanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "old_batteries")
data class OldBatteryEntity(

    @PrimaryKey(autoGenerate = true)
    val oldBatteryId: Long = 0L,

    /** null = direct entry (not linked to sale) */
    val saleId: Long? = null,

    /** Display / identification */
    val name: String,          // e.g. "Exide 150Ah"
    val brand: String,         // e.g. "Exide"
    val batteryType: String,   // e.g. "Lead Acid"

    /** Business data */
    val quantity: Int,         // number of batteries
    val rate: Double,          // rate per battery

    /** Optional, NOT used in calculation */
    val weight: Double? = null, // e.g. total weight or per battery weight
    val note: String? = null    // any remarks
) {

    /** ✅ ALWAYS CALCULATED — NEVER STORED */
    val amount: Double
        get() = quantity * rate
}

