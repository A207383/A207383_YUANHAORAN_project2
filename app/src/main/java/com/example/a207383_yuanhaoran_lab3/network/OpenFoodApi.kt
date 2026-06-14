package com.example.a207383_yuanhaoran_lab3.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 1. 定义从网络接收到的 JSON 数据的映射类
data class ProductResponse(val product: ProductInfo?)
data class ProductInfo(
    val product_name: String?,
    val packaging: String?
)

// 2. 定义向公共 API 发起网络请求的接口
interface OpenFoodApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductResponse
}

// 3. 创建 Retrofit 单例加载对象
object RetrofitClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    val api: OpenFoodApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodApi::class.java)
    }
}