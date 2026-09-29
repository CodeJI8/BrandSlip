package com.generate.brandslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.generate.brandslip.AppViewModelFactory
import com.generate.brandslip.viewmodel.BrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandSettingsScreen(navController: NavController, factory: AppViewModelFactory) {
    val viewModel: BrandViewModel = viewModel(factory = factory)
    val brandInfo by viewModel.brandInfo.collectAsState()
    
    var name by remember(brandInfo) { mutableStateOf(brandInfo.name) }
    var email by remember(brandInfo) { mutableStateOf(brandInfo.email) }
    var phone by remember(brandInfo) { mutableStateOf(brandInfo.phone) }
    var address by remember(brandInfo) { mutableStateOf(brandInfo.address) }
    var currency by remember(brandInfo) { mutableStateOf(brandInfo.defaultCurrency) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Brand Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Default Currency Symbol (e.g. $, €, £)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.saveBrandInfo(brandInfo.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        address = address,
                        defaultCurrency = currency
                    ))
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Settings")
            }
        }
    }
}
