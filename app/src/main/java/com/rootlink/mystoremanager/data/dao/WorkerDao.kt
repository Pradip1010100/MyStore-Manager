package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.WorkerEntity

@Dao
interface WorkerDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(worker: WorkerEntity): Long

    // UPDATE
    @Update
    suspend fun update(worker: WorkerEntity)

    // SOFT DELETE (IMPORTANT)
    @Query("UPDATE workers SET status = 'INACTIVE' WHERE workerId = :id")
    suspend fun deactivate(id: Long)

    @Query("UPDATE workers SET status = 'ACTIVE' WHERE workerId = :id")
    suspend fun activate(id: Long)

    @Query("SELECT * FROM workers")
    suspend fun getAll(): List<WorkerEntity>

    // READ
    @Query("SELECT * FROM workers WHERE workerId = :id")
    suspend fun getById(id: Long): WorkerEntity

    @Query("SELECT * FROM workers WHERE status = 'ACTIVE'")
    suspend fun getActive(): List<WorkerEntity>
}
