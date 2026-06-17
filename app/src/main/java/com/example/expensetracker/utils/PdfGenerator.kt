package com.example.expensetracker.utils

import android.content.Context
import android.os.Environment
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.ui.theme.Contact
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    fun generateContactReport(context: Context, contact: Contact, transactions: List<ExpenseEntity>): File? {
        val fileName = "Report_${contact.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            val writer = PdfWriter(filePath)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            // Header
            document.add(Paragraph("Transaction Report").setBold().setFontSize(20f).setTextAlignment(TextAlignment.CENTER))
            document.add(Paragraph("Contact: ${contact.name}").setFontSize(14f))
            document.add(Paragraph("Phone: ${contact.phone}").setFontSize(12f))
            document.add(Paragraph("Generated on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}").setFontSize(10f))
            document.add(Paragraph("\n"))

            // Table
            val table = Table(UnitValue.createPointArray(floatArrayOf(100f, 150f, 80f, 80f, 80f)))
            table.width = UnitValue.createPercentValue(100f)

            // Table Headers
            table.addHeaderCell(Cell().add(Paragraph("Date").setBold()))
            table.addHeaderCell(Cell().add(Paragraph("Description").setBold()))
            table.addHeaderCell(Cell().add(Paragraph("You Gave").setBold()))
            table.addHeaderCell(Cell().add(Paragraph("You Got").setBold()))
            table.addHeaderCell(Cell().add(Paragraph("Balance").setBold()))

            var runningBalance = 0.0
            val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault())

            transactions.sortedBy { it.date }.forEach { transaction ->
                if (transaction.isExpense) {
                    runningBalance += transaction.amount
                } else {
                    runningBalance -= transaction.amount
                }

                table.addCell(Cell().add(Paragraph(sdf.format(Date(transaction.date)))))
                table.addCell(Cell().add(Paragraph(transaction.title)))
                table.addCell(Cell().add(Paragraph(if (transaction.isExpense) "₹${transaction.amount}" else "-")))
                table.addCell(Cell().add(Paragraph(if (!transaction.isExpense) "₹${transaction.amount}" else "-")))
                
                val balCell = Cell().add(Paragraph("₹${Math.abs(runningBalance)}"))
                if (runningBalance >= 0) {
                    balCell.setFontColor(ColorConstants.GREEN)
                } else {
                    balCell.setFontColor(ColorConstants.RED)
                }
                table.addCell(balCell)
            }

            document.add(table)

            // Summary
            val totalGave = transactions.filter { it.isExpense }.sumOf { it.amount }
            val totalGot = transactions.filter { !it.isExpense }.sumOf { it.amount }
            val finalBalance = totalGave - totalGot

            document.add(Paragraph("\nSummary").setBold().setFontSize(14f))
            document.add(Paragraph("Total You Gave: ₹$totalGave"))
            document.add(Paragraph("Total You Got: ₹$totalGot"))
            val netText = if (finalBalance >= 0) "Net (You will get): ₹$finalBalance" else "Net (You will give): ₹${Math.abs(finalBalance)}"
            document.add(Paragraph(netText).setBold().setFontColor(if (finalBalance >= 0) ColorConstants.GREEN else ColorConstants.RED))

            document.close()
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
