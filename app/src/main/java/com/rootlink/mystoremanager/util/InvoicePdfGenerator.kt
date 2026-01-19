package com.rootlink.mystoremanager.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import java.io.File
import java.io.FileOutputStream

object InvoicePdfGenerator {

    fun generate(
        context: Context,
        sale: SaleEntity,
        items: List<SaleItemEntity>,
        productNameMap: Map<Long, String>
    ): File {

        val pdf = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        var y = 40

        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("S N Enterprises", 40f, y.toFloat(), paint)

        y += 25
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Battery & Inverter Store", 40f, y.toFloat(), paint)

        y += 30
        canvas.drawText("Invoice No: INV-${sale.saleId}", 40f, y.toFloat(), paint)
        canvas.drawText("Date: ${sale.saleDate}", 350f, y.toFloat(), paint)

        y += 30
        paint.isFakeBoldText = true
        canvas.drawText("Item", 40f, y.toFloat(), paint)
        canvas.drawText("Qty", 300f, y.toFloat(), paint)
        canvas.drawText("Amount", 450f, y.toFloat(), paint)

        paint.isFakeBoldText = false
        y += 15
        canvas.drawLine(40f, y.toFloat(), 550f, y.toFloat(), paint)

        y += 20

        items.forEach {
            canvas.drawText(productNameMap[it.productId] ?: "Product", 40f, y.toFloat(), paint)
            canvas.drawText(it.quantity.toString(), 300f, y.toFloat(), paint)
            canvas.drawText("₹${it.lineTotal}", 450f, y.toFloat(), paint)
            y += 18
        }

        y += 20
        canvas.drawLine(40f, y.toFloat(), 550f, y.toFloat(), paint)

        y += 25
        paint.isFakeBoldText = true
        canvas.drawText("Total: ₹${sale.finalAmount}", 350f, y.toFloat(), paint)

        pdf.finishPage(page)

        val file = File(context.cacheDir, "invoice_${sale.saleId}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}
