package com.example.burnerchat.firebase.repositories

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

object ImageUtils {
    fun convertToBase64(bitmapIcon: Bitmap): String {
        val output = ByteArrayOutputStream()
        bitmapIcon.compress(Bitmap.CompressFormat.JPEG, 100, output)
        val byteArray = output.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeFromBase64(base64String: String): Bitmap {
        val decoded = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    }

    fun loadBitmapFromURI(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // Establece la imagen redimensionada con borde redondeado en un ImageView
    fun setImageWithRoundedBorder(context: Context, icon: String, ivIcon: ImageView, dp: Int) {
        // Convertimos dp a píxeles
        val maxSizePx = dpToPx(context, dp)

        // Decodificamos la imagen desde Base64
        val bitmap = decodeFromBase64(icon)

        // Comprobamos las dimensiones de la imagen
        val width = bitmap.width
        val height = bitmap.height

        // Calculamos el factor de escala necesario para ajustar la imagen al tamaño máximo
        val scale = (maxSizePx.toFloat() / width).coerceAtMost(maxSizePx.toFloat() / height)

        // Redimensionamos la imagen solo si es necesario
        val scaledBitmap = if (scale < 1f) {
            // Si la escala es menor que 1, redimensionamos la imagen
            Bitmap.createScaledBitmap(
                bitmap,
                (width * scale).toInt(),
                (height * scale).toInt(),
                true
            )
        } else {
            // Si la escala es mayor o igual a 1, la imagen no necesita redimensionarse
            bitmap
        }

        // Asignamos la imagen redimensionada al ImageView
        ivIcon.setImageBitmap(scaledBitmap)
    }

    // Función para convertir dp a píxeles
    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}