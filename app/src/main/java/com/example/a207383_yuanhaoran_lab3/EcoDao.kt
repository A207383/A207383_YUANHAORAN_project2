package com.example.a207383_yuanhaoran_lab3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoDao {

    // 1. 插入数据的方法（如果遇到重复的 ID，则忽略）
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: EcoProduct)

    // 2. 获取所有数据的方法，必须返回 Flow<List<Entity>> 格式 [cite: 47]
    // 这里我们按 ID 倒序（DESC）排列，这样最新添加的物品会显示在列表最上面
    @Query("SELECT * FROM eco_product_table ORDER BY id DESC")
    fun getAll(): Flow<List<EcoProduct>>
}