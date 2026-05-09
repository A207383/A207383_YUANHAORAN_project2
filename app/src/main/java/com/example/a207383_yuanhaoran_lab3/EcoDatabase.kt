package com.example.a207383_yuanhaoran_lab3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. 声明数据库，绑定我们之前创建的表 (EcoProduct)，版本号设为 1
@Database(entities = [EcoProduct::class], version = 1, exportSchema = false)
abstract class EcoDatabase : RoomDatabase() {

    // 2. 将之前写好的 DAO 接口连接到数据库中
    abstract fun ecoDao(): EcoDao

    // 3. 伴生对象 (Companion Object)，用于全局获取数据库的单例
    companion object {
        @Volatile
        private var Instance: EcoDatabase? = null

        fun getDatabase(context: Context): EcoDatabase {
            // 如果 Instance 不为空就直接返回，如果为空就创建一个新的数据库实例
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "eco_database"
                )

                    // 允许破坏性迁移：如果以后修改了表结构（比如加了新字段），直接清空旧数据重建表，防止崩溃
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}