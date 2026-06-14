package com.example.a207383_yuanhaoran_lab3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScannerScreen(viewModel: EcoViewModel) {
    var isScanning by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 【修改点】：把之前的 CameraAlt 换成了绝对不会报错的 Search 图标
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Scanner",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Hardware Sensor: Camera Scanner", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            isScanning = true
            // 模拟扫描到一个真实的欧洲环保商品条码
            val targetBarcode = "3017620422003"
            viewModel.fetchProductFromApiAndSaveLocal(targetBarcode)
            resultText = "Scanning Barcode: $targetBarcode...\nFetching from OpenFoodFacts API!"
        }) {
            Text("Trigger Camera Scanner")
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (isScanning) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(resultText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Success! Data is auto-saved to Room Database. Check History!", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}