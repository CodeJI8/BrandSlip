package com.generate.brandslip.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.util.Log
import com.generate.brandslip.data.Receipt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator(private val context: Context) {
    private val TAG = "PdfGenerator"

    suspend fun generateReceiptPdf(receipt: Receipt): File? = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting PDF generation for receipt: ${receipt.receiptNumber}")
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        try {
            Log.d(TAG, "Drawing receipt on canvas...")
            drawReceipt(canvas, receipt)
            pdfDocument.finishPage(page)

            val outputDir = File(context.cacheDir, "receipts")
            if (!outputDir.exists()) {
                val created = outputDir.mkdirs()
                Log.d(TAG, "Created receipts directory: $created")
            }
            
            val fileName = "receipt_${receipt.receiptNumber.replace("[^a-zA-Z0-9.-]".toRegex(), "_")}.pdf"
            val file = File(outputDir, fileName)
            Log.d(TAG, "Writing PDF to file: ${file.absolutePath}")
            
            pdfDocument.writeTo(FileOutputStream(file))
            Log.d(TAG, "PDF generation successful.")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error generating PDF: ${e.message}", e)
            null
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawReceipt(canvas: Canvas, receipt: Receipt) {
        val paint = Paint()
        var yPosition = 50f
        val leftMargin = 50f
        val rightMargin = 545f
        
        // Brand Name
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(receipt.brandName.ifEmpty { "RECEIPT" }, leftMargin, yPosition, paint)
        yPosition += 30f

        // Brand Info
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        paint.color = Color.DKGRAY
        if (receipt.brandAddress.isNotEmpty()) {
            canvas.drawText(receipt.brandAddress, leftMargin, yPosition, paint)
            yPosition += 15f
        }
        if (receipt.brandEmail.isNotEmpty() || receipt.brandPhone.isNotEmpty()) {
            canvas.drawText("${receipt.brandEmail} | ${receipt.brandPhone}", leftMargin, yPosition, paint)
            yPosition += 25f
        }
        
        // Divider
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1f
        canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, paint)
        yPosition += 30f

        // Receipt details
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText("RECEIPT #", rightMargin - 150f, yPosition, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(receipt.receiptNumber, rightMargin - 50f, yPosition, paint)
        yPosition += 20f

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DATE:", rightMargin - 150f, yPosition, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(sdf.format(Date(receipt.dateMillis)), rightMargin - 50f, yPosition, paint)
        yPosition += 30f

        // Customer Info
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText("BILL TO:", leftMargin, yPosition, paint)
        yPosition += 20f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        if (receipt.customerName.isNotEmpty()) {
            canvas.drawText(receipt.customerName, leftMargin, yPosition, paint)
            yPosition += 15f
        }
        if (receipt.customerEmail.isNotEmpty()) {
            canvas.drawText(receipt.customerEmail, leftMargin, yPosition, paint)
            yPosition += 15f
        }
        if (receipt.customerPhone.isNotEmpty()) {
            canvas.drawText(receipt.customerPhone, leftMargin, yPosition, paint)
            yPosition += 15f
        }
        yPosition += 30f
        
        // Custom Fields
        if (receipt.customFields.isNotEmpty()) {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("ADDITIONAL INFO:", leftMargin, yPosition, paint)
            yPosition += 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            for (field in receipt.customFields) {
                canvas.drawText("${field.name}: ${field.value}", leftMargin, yPosition, paint)
                yPosition += 15f
            }
            yPosition += 15f
        }

        // Table Header
        paint.color = Color.LTGRAY
        canvas.drawRect(leftMargin, yPosition, rightMargin, yPosition + 25f, paint)
        
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        yPosition += 18f
        canvas.drawText("Item", leftMargin + 10f, yPosition, paint)
        canvas.drawText("Qty", rightMargin - 180f, yPosition, paint)
        canvas.drawText("Price", rightMargin - 120f, yPosition, paint)
        canvas.drawText("Total", rightMargin - 50f, yPosition, paint)
        yPosition += 15f

        // Table Rows
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        for (item in receipt.items) {
            canvas.drawText(item.name, leftMargin + 10f, yPosition, paint)
            canvas.drawText(item.quantity.toString(), rightMargin - 180f, yPosition, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.unitPrice), rightMargin - 120f, yPosition, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.total), rightMargin - 50f, yPosition, paint)
            yPosition += 20f
        }
        
        yPosition += 20f
        paint.color = Color.LTGRAY
        canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, paint)
        yPosition += 20f
        
        // Summary
        paint.color = Color.BLACK
        val summaryX = rightMargin - 150f
        val valueX = rightMargin - 50f
        
        canvas.drawText("Subtotal:", summaryX, yPosition, paint)
        canvas.drawText(String.format(Locale.getDefault(), "%s%.2f", receipt.currency, receipt.subtotal), valueX, yPosition, paint)
        yPosition += 20f
        
        if (receipt.discountAmount > 0) {
            canvas.drawText("Discount:", summaryX, yPosition, paint)
            canvas.drawText(String.format(Locale.getDefault(), "-%s%.2f", receipt.currency, receipt.discountAmount), valueX, yPosition, paint)
            yPosition += 20f
        }
        
        if (receipt.taxAmount > 0) {
            canvas.drawText("Tax (%.2f%%):".format(receipt.taxRate), summaryX, yPosition, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%s%.2f", receipt.currency, receipt.taxAmount), valueX, yPosition, paint)
            yPosition += 20f
        }
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        canvas.drawText("TOTAL:", summaryX, yPosition, paint)
        canvas.drawText(String.format(Locale.getDefault(), "%s%.2f", receipt.currency, receipt.total), valueX, yPosition, paint)
    }
}
