package com.example.lp_logistics.presentation.components.QR

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream
import java.io.OutputStream


fun generateQRCodeBitmap(text: String, width: Int = 500, height: Int = 500): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        width,
        height,
    )

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.Black.toArgb() else Color.White.toArgb())
        }
    }

    return bitmap
}

fun createPdfWithQRCodes(ids: List<String>): ByteArray {
    val stream = ByteArrayOutputStream()

    // Initialize PDF writer and document
    val writer = PdfWriter(stream)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    // Add each QR code to the PDF
    ids.forEach { id ->
        // Generate QR code bitmap
        val qrCodeBitmap = generateQRCodeBitmap(id)

        // Convert bitmap to byte array
        val bitmapStream = ByteArrayOutputStream()
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream)
        val qrCodeByteArray = bitmapStream.toByteArray()

        // Add QR code image to PDF
        val qrCodeImage = Image(ImageDataFactory.create(qrCodeByteArray))
        document.add(Paragraph("ID: $id"))
        document.add(qrCodeImage)
    }

    // Close the document
    document.close()

    // Return the PDF content as a ByteArray
    return stream.toByteArray()
}

fun savePdfToDownloads(context: Context, fileName: String, content: ByteArray): Uri? {
    val resolver = context.contentResolver

    // Define the file details
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
    }

    // Insert the file into the MediaStore
    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    uri?.let {
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            outputStream?.use { stream ->
                stream.write(content)
            }
            return uri
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return null
}