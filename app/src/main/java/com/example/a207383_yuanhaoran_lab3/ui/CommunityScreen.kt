package com.example.a207383_yuanhaoran_lab3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommunityScreen(viewModel: EcoViewModel) {
    val communityItems by viewModel.communityList.collectAsState()

    // 每次进入这个页面，自动触发 ViewModel 去 Firebase 拉取最新数据
    LaunchedEffect(Unit) {
        viewModel.fetchCommunityProducts()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🌍 Global Eco Share-Board", style = MaterialTheme.typography.headlineMedium)
        Text("Live Sync data powered by Firebase", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (communityItems.isEmpty()) {
            CircularProgressIndicator() // 数据加载中显示转圈圈
        } else {
            LazyColumn {
                items(communityItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Product: ${item.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Status: ${item.score}", color = MaterialTheme.colorScheme.primary)
                            Text("Details: ${item.description}")
                        }
                    }
                }
            }
        }
    }
}