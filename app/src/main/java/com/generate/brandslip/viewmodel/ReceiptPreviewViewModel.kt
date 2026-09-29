package com.generate.brandslip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.generate.brandslip.data.AppRepository
import com.generate.brandslip.data.Receipt
import com.generate.brandslip.pdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ReceiptPreviewViewModel(
    private val repository: AppRepository,
    private val pdfGenerator: PdfGenerator
) : ViewModel() {

    private val _receipt = MutableStateFlow<Receipt?>(null)
    val receipt: StateFlow<Receipt?> = _receipt.asStateFlow()

    private val _pdfFile = MutableStateFlow<File?>(null)
    val pdfFile: StateFlow<File?> = _pdfFile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadReceipt(receiptId: String?) {
        if (receiptId == null) {
            _error.value = "Invalid receipt ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val loadedReceipt = repository.getReceiptById(receiptId)
                if (loadedReceipt == null) {
                    _error.value = "Receipt not found"
                } else {
                    _receipt.value = loadedReceipt
                    val file = pdfGenerator.generateReceiptPdf(loadedReceipt)
                    if (file == null) {
                        _error.value = "Failed to generate PDF"
                    } else {
                        _pdfFile.value = file
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
