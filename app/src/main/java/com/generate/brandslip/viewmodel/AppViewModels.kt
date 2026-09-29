package com.generate.brandslip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.generate.brandslip.data.AppRepository
import com.generate.brandslip.data.BrandInfo
import com.generate.brandslip.data.Receipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrandViewModel(private val repository: AppRepository) : ViewModel() {
    private val _brandInfo = MutableStateFlow(BrandInfo())
    val brandInfo: StateFlow<BrandInfo> = _brandInfo.asStateFlow()

    init {
        loadBrandInfo()
    }

    private fun loadBrandInfo() {
        viewModelScope.launch {
            _brandInfo.value = repository.getBrandInfo()
        }
    }

    fun saveBrandInfo(info: BrandInfo) {
        viewModelScope.launch {
            repository.saveBrandInfo(info)
            _brandInfo.value = info
        }
    }
}

class ReceiptListViewModel(private val repository: AppRepository) : ViewModel() {
    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    val receipts: StateFlow<List<Receipt>> = _receipts.asStateFlow()

    fun loadReceipts() {
        viewModelScope.launch {
            _receipts.value = repository.getReceipts().sortedByDescending { it.dateMillis }
        }
    }

    fun deleteReceipt(id: String) {
        viewModelScope.launch {
            repository.deleteReceipt(id)
            loadReceipts()
        }
    }
}
