package com.example.a207383_yuanhaoran_lab3.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207383_yuanhaoran_lab3.R
import com.example.a207383_yuanhaoran_lab3.data.DemoItem

@Composable
fun HomeScreen(
    onStartScan: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToHistory: () -> Unit,
    onGoToScanner: () -> Unit,   // 🔥 新增：接收跳转到硬件扫描页的指令
    onGoToCommunity: () -> Unit  // 🔥 新增：接收跳转到云端社区页的指令
) {
    // 使用你提供的图片资源名
    val productList = listOf(
        DemoItem("Organic Cotton", "SDG 12 Impact: High", R.drawable.eco_leaf),
        DemoItem("Plastic Waste", "Recycle Priority: Urgent", R.drawable.recycle_icon),
        DemoItem("Sample Product", "Verification Needed", R.drawable.sample_item)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(painterResource(id = R.drawable.ic_scan), "Add") }, label = { Text("Add Item") }, selected = false, onClick = onStartScan)
                NavigationBarItem(icon = { Icon(painterResource(id = R.drawable.ic_settings), "Settings") }, label = { Text("Settings") }, selected = false, onClick = onGoToSettings)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                // 用户个人信息栏
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.user_avatar),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Welcome Back,", fontSize = 12.sp, color = Color.Gray)
                        Text("Yuan Haoran", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 顶部横幅 Banner
                Card(shape = RoundedCornerShape(20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.eco_banner),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🔥 新增的 Project 2 专属功能入口按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedButton(
                        onClick = onGoToScanner,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Scanner", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scanner")
                    }

                    ElevatedButton(
                        onClick = onGoToCommunity,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Community", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Community")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Eco Insights", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onGoToHistory) {
                        Text("My History >", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(productList) { item ->
                ExpandableProductCard(item)
            }
        }
    }
}

@Composable
fun ExpandableProductCard(item: DemoItem) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = item.iconRes), contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text(item.subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
        }
    }
}