package com.generate.brandslip.data

import java.util.UUID

data class BrandInfo(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val website: String = "",
    val logoUri: String = "",
    val defaultTaxRate: Double = 0.0,
    val defaultCurrency: String = "$"
)

data class CustomField(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val value: String
)

data class ReceiptItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0
) {
    val total: Double get() = quantity * unitPrice
}

data class Receipt(
    val id: String = UUID.randomUUID().toString(),
    val receiptNumber: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    
    // Customer
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    
    // Items
    val items: List<ReceiptItem> = emptyList(),
    val customFields: List<CustomField> = emptyList(),
    
    // Amounts
    val subtotal: Double = 0.0,
    val discountRate: Double = 0.0, // Percentage e.g. 10.0 for 10%
    val discountAmount: Double = 0.0, // Fixed amount discount
    val taxRate: Double = 0.0, // Percentage
    val taxAmount: Double = 0.0, // Fixed or calculated
    val total: Double = 0.0,
    
    // Snapshot of brand at time of receipt
    val brandName: String = "",
    val brandAddress: String = "",
    val brandEmail: String = "",
    val brandPhone: String = "",
    val brandLogoUri: String = "",
    val currency: String = "$"
)
