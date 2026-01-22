package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity

@Dao
interface CompanyProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(profile: CompanyProfileEntity)

    @Query("SELECT * FROM company_profile WHERE id = 1")
    suspend fun get(): CompanyProfileEntity?
}
