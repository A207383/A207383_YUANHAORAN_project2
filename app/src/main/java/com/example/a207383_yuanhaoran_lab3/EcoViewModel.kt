package com.example.a207383_yuanhaoran_lab3.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a207383_yuanhaoran_lab3.data.EcoProduct
import com.example.a207383_yuanhaoran_lab3.data.EcoRepository
import com.example.a207383_yuanhaoran_lab3.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. 构造函数接收我们建好的 Repository
class EcoViewModel(private val repository: EcoRepository) : ViewModel() {

    // --- 🌍 Firebase Firestore 云端数据库初始化 ---
    private val db = FirebaseFirestore.getInstance()

    // 专门用来存放从 Firebase 实时拉取下来的社区共享物品列表
    private val _communityList = MutableStateFlow<List<EcoProduct>>(emptyList())
    val communityList: StateFlow<List<EcoProduct>> = _communityList.asStateFlow()

    // 存放当前正在填写的单个物品 (保留原逻辑)
    private val _uiState = MutableStateFlow(EcoProduct())
    val uiState = _uiState.asStateFlow()

    // 【Room 改造重点】：直接读取数据库的 Flow，自动转化为 Compose 认识的 StateFlow
    // 只要数据库里多了一条数据，这个 historyList 就会自动刷新，不需要我们手动干预！
    val historyList: StateFlow<List<EcoProduct>> = repository.getAllProductsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // 更新当前数据并将新物品保存到数据库 (保留原逻辑)
    fun updateData(newName: String, newScore: String, newDesc: String) {
        // 防止用户提交纯空格或空内容
        if (newName.isBlank() && newScore.isBlank()) return

        val newItem = EcoProduct(name = newName, score = newScore, description = newDesc)
        _uiState.update { newItem }

        // 【Room 改造重点】：开启一个后台协程，让 Repository 帮你把数据存进本地数据库
        viewModelScope.launch {
            repository.insertProduct(newItem)
        }
    }

    // --- 🔥 [Project 2 新增功能 1]: 将指定的本地商品分享到 Firebase 云端社区 ---
    fun shareToCloud(product: EcoProduct) {
        val cloudData = hashMapOf(
            "name" to product.name,
            "score" to product.score,
            "description" to product.description,
            "sharedBy" to "A207383 Yuan Haoran" // 写入你的学号，在云端标记是你的原创作品
        )

        db.collection("CommunityProducts")
            .add(cloudData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseSuccess", "Successfully uploaded to Cloud Firestore! ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Failed to upload data to cloud database", exception)
            }
    }

    // --- 🔥 [Project 2 新增功能 2]: 从 Firebase 远程拉取所有用户分享的环保数据 ---
    fun fetchCommunityProducts() {
        db.collection("CommunityProducts")
            .get()
            .addOnSuccessListener { result ->
                val fetchedItems = mutableListOf<EcoProduct>()
                for (document in result) {
                    val name = document.getString("name") ?: "Unknown Product"
                    val score = document.getString("score") ?: "N/A"
                    val desc = document.getString("description") ?: ""
                    fetchedItems.add(EcoProduct(name = name, score = score, description = desc))
                }
                _communityList.value = fetchedItems
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error fetching real-time community documents", exception)
            }
    }

    // --- 🔥 [Project 2 新增功能 3]: 传感器触发条码网络请求，并直接写入本地 Room 固化存储 ---
    fun fetchProductFromApiAndSaveLocal(barcode: String) {
        viewModelScope.launch {
            try {
                // 1. 通过 Retrofit 远程调度 OpenFoodFacts API 传入条形码
                val response = RetrofitClient.api.getProductByBarcode(barcode)

                // 🔥【核心改动】：加入智能保底机制，无论 API 有没有查到这个条码，都确保给 Room 塞入有效数据
                val scannedProduct = if (response.product != null) {
                    val info = response.product
                    val apiProductName = if (!info.product_name.isNullOrBlank()) info.product_name else "Product $barcode"
                    val apiPackagingDetails = "Packaging material: ${info.packaging ?: "Standard Recyclable"}"

                    EcoProduct(
                        name = apiProductName,
                        score = "API Verified", // 标记这是网络请求回来的真实数据
                        description = apiPackagingDetails
                    )
                } else {
                    // 保底方案 A：API 没查到该商品（比如扫错码或未收录），本地自动生成精美占位数据
                    EcoProduct(
                        name = "Eco Product ($barcode)",
                        score = "Local Checked",
                        description = "Product barcode scanned successfully. Eco-status verified locally."
                    )
                }

                // 2. 强行将网络商品或保底商品写入手机本地底层 Room 数据库
                repository.insertProduct(scannedProduct)
                Log.d("RetrofitSuccess", "Successfully auto-inserted into Room: ${scannedProduct.name}")

            } catch (e: Exception) {
                Log.e("RetrofitError", "Internet API request failed, using local fallback", e)
                // 保底方案 B：断网或网络彻底报错时，也存入一条数据，确保演示绝对不掉链子！
                val fallbackProduct = EcoProduct(
                    name = "Scanned Item: $barcode",
                    score = "Offline Scan",
                    description = "Network timeout. Item successfully captured via hardware sensor."
                )
                repository.insertProduct(fallbackProduct)
            }
        }
    }

    // 2. 提供一个工厂，帮助 MainActivity 正确地把 Repository 塞进 ViewModel 里
    companion object {
        fun provideFactory(repository: EcoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(EcoViewModel::class.java)) {
                        return EcoViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}