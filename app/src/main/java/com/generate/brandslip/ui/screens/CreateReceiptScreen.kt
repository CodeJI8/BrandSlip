package com.generate.brandslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.generate.brandslip.AppViewModelFactory
import com.generate.brandslip.data.ReceiptItem
import com.generate.brandslip.viewmodel.CreateReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptScreen(navController: NavController, factory: AppViewModelFactory) {
    val viewModel: CreateReceiptViewModel = viewModel(factory = factory)
    val receipt by viewModel.receipt.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Receipt") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Customer Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = receipt.customerName,
                        onValueChange = { viewModel.updateReceipt(receipt.copy(customerName = it)) },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = receipt.customerEmail,
                        onValueChange = { viewModel.updateReceipt(receipt.copy(customerEmail = it)) },
                        label = { Text("Customer Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { showAddItemDialog = true }) {
                            Text("Add Item")
                        }
                    }
                }

                items(receipt.items) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("${item.quantity} x ${receipt.currency}${String.format("%.2f", item.unitPrice)}")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(String.format("%.2f", item.total), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { viewModel.removeItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Amounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = if (receipt.taxRate == 0.0) "" else receipt.taxRate.toString(),
                        onValueChange = {
                            val rate = it.toDoubleOrNull() ?: 0.0
                            viewModel.updateReceipt(receipt.copy(taxRate = rate))
                        },
                        label = { Text("Tax Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = if (receipt.discountAmount == 0.0) "" else receipt.discountAmount.toString(),
                        onValueChange = {
                            val amount = it.toDoubleOrNull() ?: 0.0
                            viewModel.updateReceipt(receipt.copy(discountAmount = amount))
                        },
                        label = { Text("Discount Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal:")
                                Text(String.format("%.2f", receipt.subtotal))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax:")
                                Text(String.format("%.2f", receipt.taxAmount))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total:", fontWeight = FontWeight.Bold)
                                Text(String.format("%.2f", receipt.total), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            var isGenerating by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    if (!isGenerating) {
                        isGenerating = true
                        viewModel.saveReceipt { savedReceipt ->
                            isGenerating = false
                            navController.navigate("receipt_preview?receiptId=${savedReceipt.id}")
                        }
                    }
                },
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Generate Receipt")
                }
            }
        }

        if (showAddItemDialog) {
            AddItemDialog(
                onDismiss = { showAddItemDialog = false },
                onAdd = { name, qty, price ->
                    viewModel.addItem(ReceiptItem(name = name, quantity = qty, unitPrice = price))
                    showAddItemDialog = false
                }
            )
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, Int, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var qtyStr by remember { mutableStateOf("1") }
    var priceStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") })
                OutlinedTextField(
                    value = qtyStr,
                    onValueChange = { qtyStr = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Unit Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val qty = qtyStr.toIntOrNull() ?: 1
                val price = priceStr.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank()) {
                    onAdd(name, qty, price)
                }
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
