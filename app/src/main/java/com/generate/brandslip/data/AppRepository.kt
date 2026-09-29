package com.generate.brandslip.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AppRepository(private val context: Context) {
    private val gson = Gson()
    
    private val brandInfoFile = File(context.filesDir, "brand_info.json")
    private val receiptsFile = File(context.filesDir, "receipts.json")

    suspend fun getBrandInfo(): BrandInfo = withContext(Dispatchers.IO) {
        if (brandInfoFile.exists()) {
            try {
                val json = brandInfoFile.readText()
                gson.fromJson(json, BrandInfo::class.java) ?: BrandInfo()
            } catch (e: Exception) {
                e.printStackTrace()
                BrandInfo()
            }
        } else {
            BrandInfo()
        }
    }

    suspend fun saveBrandInfo(info: BrandInfo) = withContext(Dispatchers.IO) {
        val json = gson.toJson(info)
        brandInfoFile.writeText(json)
    }

    suspend fun getReceipts(): List<Receipt> = withContext(Dispatchers.IO) {
        if (receiptsFile.exists()) {
            try {
                val json = receiptsFile.readText()
                val type = object : TypeToken<List<Receipt>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    suspend fun getReceiptById(id: String): Receipt? = withContext(Dispatchers.IO) {
        getReceipts().find { it.id == id }
    }

    suspend fun saveReceipt(receipt: Receipt) = withContext(Dispatchers.IO) {
        val receipts = getReceipts().toMutableList()
        val index = receipts.indexOfFirst { it.id == receipt.id }
        if (index != -1) {
            receipts[index] = receipt
        } else {
            receipts.add(receipt)
        }
        val json = gson.toJson(receipts)
        receiptsFile.writeText(json)
    }

    suspend fun deleteReceipt(id: String) = withContext(Dispatchers.IO) {
        val receipts = getReceipts().toMutableList()
        receipts.removeAll { it.id == id }
        val json = gson.toJson(receipts)
        receiptsFile.writeText(json)
    }
    
    suspend fun generateNextReceiptNumber(): String = withContext(Dispatchers.IO) {
        val count = getReceipts().size
        String.format("REC-%04d", count + 1)
    }
}
