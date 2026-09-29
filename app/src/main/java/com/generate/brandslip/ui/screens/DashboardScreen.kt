package com.generate.brandslip.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.generate.brandslip.AppViewModelFactory
import com.generate.brandslip.data.Receipt
import com.generate.brandslip.viewmodel.ReceiptListViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, factory: AppViewModelFactory) {
    val viewModel: ReceiptListViewModel = viewModel(factory = factory)
    val receipts by viewModel.receipts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReceipts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Brandly Receipts") },
                actions = {
                    IconButton(onClick = { navController.navigate("brand_settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Brand Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_receipt") }) {
                Icon(Icons.Default.Add, contentDescription = "Create Receipt")
            }
        }
    ) { padding ->
        if (receipts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No receipts yet. Create one!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(receipts) { receipt ->
                    ReceiptCard(receipt = receipt, onClick = {
                        navController.navigate("receipt_preview?receiptId=${receipt.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun ReceiptCard(receipt: Receipt, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(receipt.receiptNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(receipt.customerName.ifEmpty { "Unknown Customer" }, style = MaterialTheme.typography.bodyMedium)
                Text(sdf.format(Date(receipt.dateMillis)), style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = String.format("%.2f", receipt.total),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
