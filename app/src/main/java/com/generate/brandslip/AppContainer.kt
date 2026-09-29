package com.generate.brandslip

import android.content.Context
import com.generate.brandslip.data.AppRepository
import com.generate.brandslip.pdf.PdfGenerator

class AppContainer(private val context: Context) {
    val repository: AppRepository by lazy { AppRepository(context) }
    val pdfGenerator: PdfGenerator by lazy { PdfGenerator(context) }
}
