package com.example.a207383_yuanhaoran_lab3.data

// ✅ 新增的两个导入，专门用于 Room 数据库
import androidx.room.Entity
import androidx.room.PrimaryKey

// ✅ 1. 加上 @Entity 注解，告诉 Room 这是一个数据库表，并给表起个名字
@Entity(tableName = "eco_product_table")
data class EcoProduct(
    // ✅ 2. 必须添加一个主键 (Primary Key)，autoGenerate = true 表示让数据库自动生成 1,2,3 这样的编号
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // 下面是你原本的属性，完全保留！
    val name: String = "",
    val score: String = "",
    val description: String = ""
)

// 用于主页列表显示的演示数据类（保持原样即可，这个不需要存进数据库）
data class DemoItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)