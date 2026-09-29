package com.generate.brandslip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.generate.brandslip.viewmodel.BrandViewModel
import com.generate.brandslip.viewmodel.CreateReceiptViewModel
import com.generate.brandslip.viewmodel.ReceiptListViewModel
import com.generate.brandslip.viewmodel.ReceiptPreviewViewModel

class AppViewModelFactory(private val app: BrandlyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BrandViewModel::class.java) -> {
                BrandViewModel(app.container.repository) as T
            }
            modelClass.isAssignableFrom(ReceiptListViewModel::class.java) -> {
                ReceiptListViewModel(app.container.repository) as T
            }
            modelClass.isAssignableFrom(CreateReceiptViewModel::class.java) -> {
                CreateReceiptViewModel(app.container.repository, app.container.pdfGenerator) as T
            }
            modelClass.isAssignableFrom(ReceiptPreviewViewModel::class.java) -> {
                ReceiptPreviewViewModel(app.container.repository, app.container.pdfGenerator) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

