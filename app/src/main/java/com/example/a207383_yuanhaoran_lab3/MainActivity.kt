package com.example.a207383_yuanhaoran_lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a207383_yuanhaoran_lab3.data.EcoDatabase
import com.example.a207383_yuanhaoran_lab3.data.EcoRepository
import com.example.a207383_yuanhaoran_lab3.ui.*
import com.example.a207383_yuanhaoran_lab3.ui.theme.A207383_YUANHAORAN_Lab3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A207383_YUANHAORAN_Lab3Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // 【Room 改造重点】：初始化数据库和 Repository
                    val database = EcoDatabase.getDatabase(this)
                    val repository = EcoRepository(database.ecoDao())

                    // 利用我们写好的工厂，把 repository 传给 ViewModel
                    val myViewModel: EcoViewModel = viewModel(
                        factory = EcoViewModel.provideFactory(repository)
                    )

                    // 👇 完美保留你原本的导航结构，一字未改！ 👇
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onStartScan = { navController.navigate("form") },
                                onGoToSettings = { navController.navigate("settings") },
                                onGoToHistory = { navController.navigate("history") }
                            )
                        }
                        composable("form") {
                            FormScreen(
                                viewModel = myViewModel,
                                onSave = { navController.navigate("detail") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("detail") {
                            DetailScreen(
                                viewModel = myViewModel,
                                onDone = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
                            )
                        }
                        composable("history") {
                            HistoryScreen(viewModel = myViewModel, onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}