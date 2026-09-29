package com.generate.brandslip.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.generate.brandslip.data.AppRepository
import com.generate.brandslip.data.CustomField
import com.generate.brandslip.data.Receipt
import com.generate.brandslip.data.ReceiptItem
import com.generate.brandslip.pdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CreateReceiptViewModel(
    private val repository: AppRepository,
    private val pdfGenerator: PdfGenerator
) : ViewModel() {
    private val TAG = "CreateReceiptViewModel"

    private val _receipt = MutableStateFlow(Receipt())
    val receipt: StateFlow<Receipt> = _receipt.asStateFlow()
    
    private val _generatedPdf = MutableStateFlow<File?>(null)
    val generatedPdf: StateFlow<File?> = _generatedPdf.asStateFlow()

    init {
        viewModelScope.launch {
            val brandInfo = repository.getBrandInfo()
            _receipt.value = _receipt.value.copy(
                receiptNumber = repository.generateNextReceiptNumber(),
                brandName = brandInfo.name,
                brandAddress = brandInfo.address,
                brandEmail = brandInfo.email,
                brandPhone = brandInfo.phone,
                brandLogoUri = brandInfo.logoUri,
                taxRate = brandInfo.defaultTaxRate,
                currency = brandInfo.defaultCurrency
            )
        }
    }

    fun updateReceipt(updated: Receipt) {
        val subtotal = updated.items.sumOf { it.total }
        val discountAmount = if (updated.discountRate > 0) subtotal * (updated.discountRate / 100) else updated.discountAmount
        val taxableAmount = subtotal - discountAmount
        val taxAmount = taxableAmount * (updated.taxRate / 100)
        val total = taxableAmount + taxAmount
        
        val newReceipt = updated.copy(
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            total = total
        )
        Log.d(TAG, "updateReceipt: items count = ${newReceipt.items.size}")
        newReceipt.items.forEachIndexed { index, item ->
            Log.d(TAG, "updateReceipt item[$index]: $item")
        }
        
        _receipt.value = newReceipt
    }

    fun addItem(item: ReceiptItem) {
        Log.d(TAG, "addItem called with: $item")
        val items = _receipt.value.items.toMutableList()
        items.add(item)
        updateReceipt(_receipt.value.copy(items = items))
    }

    fun removeItem(item: ReceiptItem) {
        val items = _receipt.value.items.toMutableList()
        items.remove(item)
        updateReceipt(_receipt.value.copy(items = items))
    }
    
    fun addCustomField(field: CustomField) {
        val fields = _receipt.value.customFields.toMutableList()
        fields.add(field)
        _receipt.value = _receipt.value.copy(customFields = fields)
    }
    
    fun removeCustomField(field: CustomField) {
        val fields = _receipt.value.customFields.toMutableList()
        fields.remove(field)
        _receipt.value = _receipt.value.copy(customFields = fields)
    }

    fun saveReceipt(onSaved: (Receipt) -> Unit) {
        viewModelScope.launch {
            try {
                val currentReceipt = _receipt.value
                Log.d(TAG, "saveReceipt called for receipt: ${currentReceipt.receiptNumber}")
                repository.saveReceipt(currentReceipt)
                withContext(Dispatchers.Main) {
                    onSaved(currentReceipt)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in saveReceipt: ${e.message}", e)
            }
        }
    }
    
    fun clearPdf() {
        _generatedPdf.value = null
    }
}
