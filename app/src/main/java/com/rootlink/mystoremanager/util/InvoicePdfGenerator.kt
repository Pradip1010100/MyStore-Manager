package com.rootlink.mystoremanager.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.rootlink.mystoremanager.data.entity.CompanyProfileEntity
import com.rootlink.mystoremanager.data.entity.CustomerEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object InvoicePdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT_MARGIN = 40f
    private const val RIGHT_MARGIN = 555f

    private const val ITEM_ROW_HEIGHT = 18
    private const val MAX_ITEMS_PER_PAGE = 20

    fun generate(
        context: Context,
        sale: SaleEntity,
        company: CompanyProfileEntity,
        customer: CustomerEntity?,
        items: List<SaleItemEntity>,
        productNameMap: Map<Long, String>,
        oldBatteryAmount: Double?
    ): File {

        val pdf = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val pages = items.chunked(MAX_ITEMS_PER_PAGE)

        pages.forEachIndexed { pageIndex, pageItems ->

            val pageInfo = PdfDocument.PageInfo
                .Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1)
                .create()

            val page = pdf.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            var y = 40

            /* ============================================================
               HEADER — ONLY ON FIRST PAGE
               ============================================================ */

            if (pageIndex == 0) {

                // Company name
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText(company.name, LEFT_MARGIN, y.toFloat(), paint)

                // Company details
                paint.textSize = 11f
                paint.isFakeBoldText = false
                y += 18
                canvas.drawText(company.businessType, LEFT_MARGIN, y.toFloat(), paint)

                y += 14
                canvas.drawText(company.address, LEFT_MARGIN, y.toFloat(), paint)

                y += 14
                canvas.drawText("Phone: ${company.phone}", LEFT_MARGIN, y.toFloat(), paint)

                // Invoice title
                paint.isFakeBoldText = true
                paint.textSize = 18f
                canvas.drawText("INVOICE", 460f, 40f, paint)

                // Divider
                paint.isFakeBoldText = false
                paint.textSize = 11f
                y += 20
                canvas.drawLine(LEFT_MARGIN, y.toFloat(), RIGHT_MARGIN, y.toFloat(), paint)

                // Invoice meta
                y += 22
                canvas.drawText("Invoice No: INV-${sale.saleId}", LEFT_MARGIN, y.toFloat(), paint)
                canvas.drawText(
                    "Date: ${dateFormat.format(Date(sale.saleDate))}",
                    380f,
                    y.toFloat(),
                    paint
                )

                // Bill To
                y += 26
                paint.isFakeBoldText = true
                canvas.drawText("Bill To:", LEFT_MARGIN, y.toFloat(), paint)

                paint.isFakeBoldText = false
                y += 16
                canvas.drawText(
                    "Name: ${customer?.name ?: "Walk-in Customer"}",
                    LEFT_MARGIN,
                    y.toFloat(),
                    paint
                )

                y += 14
                canvas.drawText(
                    "Phone: ${customer?.phone ?: "-"}",
                    LEFT_MARGIN,
                    y.toFloat(),
                    paint
                )

                y += 14
                canvas.drawText(
                    "Address: ${customer?.address ?: "-"}",
                    LEFT_MARGIN,
                    y.toFloat(),
                    paint
                )

                y += 26
            } else {
                // Other pages start lower
                y = 60
            }

            /* ============================================================
               TABLE HEADER
               ============================================================ */

            paint.isFakeBoldText = true
            canvas.drawText("Item", LEFT_MARGIN, y.toFloat(), paint)
            canvas.drawText("Qty", 300f, y.toFloat(), paint)
            canvas.drawText("Amount", 450f, y.toFloat(), paint)

            paint.isFakeBoldText = false
            y += 8
            canvas.drawLine(LEFT_MARGIN, y.toFloat(), RIGHT_MARGIN, y.toFloat(), paint)
            y += 18

            /* ============================================================
               ITEMS
               ============================================================ */

            pageItems.forEach { item ->
                canvas.drawText(
                    productNameMap[item.productId] ?: "Product",
                    LEFT_MARGIN,
                    y.toFloat(),
                    paint
                )
                canvas.drawText(item.quantity.toString(), 300f, y.toFloat(), paint)
                canvas.drawText(
                    "₹%.2f".format(item.lineTotal),
                    450f,
                    y.toFloat(),
                    paint
                )
                y += ITEM_ROW_HEIGHT
            }

            /* ============================================================
               TOTALS — ONLY LAST PAGE
               ============================================================ */

            if (pageIndex == pages.lastIndex) {

                y += 14
                canvas.drawLine(300f, y.toFloat(), RIGHT_MARGIN, y.toFloat(), paint)

                y += 18
                canvas.drawText("Subtotal:", 300f, y.toFloat(), paint)
                canvas.drawText(
                    "₹%.2f".format(sale.totalAmount),
                    450f,
                    y.toFloat(),
                    paint
                )

                if (oldBatteryAmount != null && oldBatteryAmount > 0) {
                    y += 16
                    canvas.drawText("Old Battery Exchange:", 300f, y.toFloat(), paint)
                    canvas.drawText(
                        "- ₹%.2f".format(oldBatteryAmount),
                        450f,
                        y.toFloat(),
                        paint
                    )
                }

                y += 16
                canvas.drawText("Discount:", 300f, y.toFloat(), paint)
                canvas.drawText(
                    "- ₹%.2f".format(sale.discount),
                    450f,
                    y.toFloat(),
                    paint
                )

                y += 18
                paint.isFakeBoldText = true
                canvas.drawLine(300f, y.toFloat(), RIGHT_MARGIN, y.toFloat(), paint)

                y += 18
                canvas.drawText("Total Amount:", 300f, y.toFloat(), paint)
                canvas.drawText(
                    "₹%.2f".format(sale.finalAmount),
                    450f,
                    y.toFloat(),
                    paint
                )

                paint.isFakeBoldText = false
                y += 40
                canvas.drawText(
                    "Thank you for your business",
                    200f,
                    y.toFloat(),
                    paint
                )
            }

            pdf.finishPage(page)
        }

        val file = File(context.cacheDir, "invoice_${sale.saleId}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}
