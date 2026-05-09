package com.example.a207383_yuanhaoran_lab3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a207383_yuanhaoran_lab3.data.EcoProduct
import com.example.a207383_yuanhaoran_lab3.data.EcoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. 构造函数接收我们建好的 Repository
class EcoViewModel(private val repository: EcoRepository) : ViewModel() {

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

    // 更新当前数据并将新物品保存到数据库
    fun updateData(newName: String, newScore: String, newDesc: String) {
        val newItem = EcoProduct(name = newName, score = newScore, description = newDesc)
        _uiState.update { newItem }

        // 【Room 改造重点】：开启一个后台协程，让 Repository 帮你把数据存进本地数据库
        viewModelScope.launch {
            repository.insertProduct(newItem)
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