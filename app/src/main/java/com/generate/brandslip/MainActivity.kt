package com.generate.brandslip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.generate.brandslip.ui.screens.DashboardScreen
import com.generate.brandslip.ui.screens.BrandSettingsScreen
import com.generate.brandslip.ui.screens.CreateReceiptScreen
import com.generate.brandslip.ui.screens.ReceiptPreviewScreen
import com.generate.brandslip.ui.theme.BrandSlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = AppViewModelFactory(application as BrandlyApplication)
        
        setContent {
            BrandSlipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(navController, factory)
                        }
                        composable("brand_settings") {
                            BrandSettingsScreen(navController, factory)
                        }
                        composable("create_receipt") {
                            CreateReceiptScreen(navController, factory)
                        }
                        composable(
                            route = "receipt_preview?receiptId={receiptId}",
                            arguments = listOf(
                                navArgument("receiptId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val receiptId = backStackEntry.arguments?.getString("receiptId")
                            ReceiptPreviewScreen(navController, factory, receiptId)
                        }
                    }
                }
            }
        }
    }
}

