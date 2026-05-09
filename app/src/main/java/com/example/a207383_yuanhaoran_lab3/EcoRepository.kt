package com.example.a207383_yuanhaoran_lab3.data

import kotlinx.coroutines.flow.Flow

// 仓库类，构造函数里传入我们之前做好的 DAO
class EcoRepository(private val ecoDao: EcoDao) {

    // 1. 提供获取所有数据流的方法，其实就是转交 DAO 的 getAll() 结果
    fun getAllProductsStream(): Flow<List<EcoProduct>> {
        return ecoDao.getAll()
    }

    // 2. 提供插入数据的方法，同样是转交调用 DAO 的 insert()
    suspend fun insertProduct(product: EcoProduct) {
        ecoDao.insert(product)
    }
}