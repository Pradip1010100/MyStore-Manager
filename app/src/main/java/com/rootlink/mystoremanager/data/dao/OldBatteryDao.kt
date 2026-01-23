package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity

@Dao
interface OldBatteryDao {

    @Insert
    suspend fun insert(oldBattery: OldBatteryEntity)

    @Query("SELECT * FROM old_batteries WHERE saleId = :saleId LIMIT 1")
    suspend fun getBySaleId(saleId: Long): OldBatteryEntity?

    @Query("""
        SELECT * FROM old_batteries
        ORDER BY oldBatteryId DESC
    """)
    suspend fun getAll(): List<OldBatteryEntity>
}
