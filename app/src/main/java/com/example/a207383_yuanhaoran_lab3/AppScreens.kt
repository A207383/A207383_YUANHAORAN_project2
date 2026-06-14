package com.example.a207383_yuanhaoran_lab3 // 👈 已经去掉了报错的 .ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share // 👈 换成了绝对安全的 Share 图标
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207383_yuanhaoran_lab3.ui.EcoViewModel

// 页面 2: Form Input (输入项目)
@Composable
fun FormScreen(viewModel: EcoViewModel, onSave: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Add New SDG Item", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = score, onValueChange = { score = it }, label = { Text("Sustainability Score (0-100)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { viewModel.updateData(name, score, desc); onSave() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("SAVE ITEM") }
    }
}

// 页面 3: Item Detail (查看刚才添加的项目)
@Composable
fun DetailScreen(viewModel: EcoViewModel, onDone: () -> Unit) {
    val data by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(painterResource(id = R.drawable.scanner_icon), contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Scan Result", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Name: ${data.name}", fontWeight = FontWeight.Bold)
                Text("Eco Score: ${data.score}/100", color = MaterialTheme.colorScheme.primary)
                Text("Note: ${data.description}")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = onDone) { Text("BACK TO HOME") }
    }
}

// 页面 4: Summary List (历史记录列表 - 核心共享数据展示)
@Composable
fun HistoryScreen(viewModel: EcoViewModel, onBack: () -> Unit) {
    val history by viewModel.historyList.collectAsState()
    val context = LocalContext.current // 🔥 获取上下文，用于显示 Toast 提示

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("My Added Items", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 左边：历史记录信息
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            Text("Sustainability Score: ${item.score}", fontSize = 12.sp)
                        }

                        // 右边：Project 2 的核心按钮 - 点击将数据上传到 Firebase 云端
                        IconButton(onClick = {
                            viewModel.shareToCloud(item)
                            Toast.makeText(context, "Uploading to Cloud...", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.Share, // 👈 换成了自带的 Share (分享) 图标
                                contentDescription = "Share to Cloud",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// 页面 5: Settings (设置页)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("App Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        ListItem(headlineContent = { Text("Notifications") }, trailingContent = { Switch(checked = true, onCheckedChange = {}) })
        ListItem(headlineContent = { Text("Eco-Scanner Guide") }, leadingContent = { Icon(painterResource(id = R.drawable.ic_search), null) })
        ListItem(headlineContent = { Text("App Version") }, supportingContent = { Text("Project 1 v1.0") })
    }
}