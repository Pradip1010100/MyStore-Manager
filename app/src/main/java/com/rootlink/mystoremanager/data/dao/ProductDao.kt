package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.ProductEntity

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET status = 'INACTIVE' WHERE productId = :id")
    suspend fun deactivate(id: Long)

    @Query("UPDATE products SET status = 'ACTIVE' WHERE productId = :id")
    suspend fun activate(id: Long)

    @Query("SELECT * FROM products WHERE productId = :id")
    suspend fun getById(id: Long): ProductEntity

    @Query("SELECT * FROM products WHERE status = 'ACTIVE'")
    suspend fun getActive(): List<ProductEntity>

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>
}
