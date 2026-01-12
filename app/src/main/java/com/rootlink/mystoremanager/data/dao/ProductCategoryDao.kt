package com.rootlink.mystoremanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rootlink.mystoremanager.data.entity.ProductCategoryEntity

@Dao
interface ProductCategoryDao {

    @Insert
    suspend fun insert(category: ProductCategoryEntity): Long

    @Update
    suspend fun update(category: ProductCategoryEntity)

    @Query("UPDATE product_categories SET status = 'INACTIVE' WHERE categoryId = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT * FROM product_categories WHERE status = 'ACTIVE'")
    suspend fun getActive(): List<ProductCategoryEntity>
}
