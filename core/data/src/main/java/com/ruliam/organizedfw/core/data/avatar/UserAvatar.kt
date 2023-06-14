package com.ruliam.organizedfw.core.data.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.ContextCompat
import com.ruliam.organizedfw.core.data.R
import javax.inject.Inject


class UserAvatar @Inject constructor(private val context: Context) {

    fun generateUserAvatar(username: String): Bitmap {
        val imageSize = 200
        val backgroundStartColor = ContextCompat.getColor(context, R.color.avatar_background_start)
        val backgroundEndColor = ContextCompat.getColor(context, R.color.avatar_background_end)
        val textColor = Color.WHITE

        val bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)

        try{
            val canvas = Canvas(bitmap)

            // Desenhar o fundo gradiente
            val gradient = LinearGradient(
                0f, 0f, imageSize.toFloat(), imageSize.toFloat(),
                backgroundStartColor, backgroundEndColor, Shader.TileMode.CLAMP
            )
            val paint = Paint()
            paint.shader = gradient
            canvas.drawRect(0f, 0f, imageSize.toFloat(), imageSize.toFloat(), paint)

            val textSize = 80f
            val typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            textPaint.color = textColor
            textPaint.textSize = textSize
            textPaint.typeface = typeface
            textPaint.textAlign = Paint.Align.CENTER

            val initials = getInitials(username)

            val centerX = imageSize.toFloat() / 2
            val centerY = imageSize.toFloat() / 2

            canvas.drawText(
                initials,
                centerX,
                centerY - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
            Log.d(TAG, "Success in create bitmap")
            return bitmap
        }
        catch (e: Exception){
            Log.w(TAG, "Error on generate avatar ${e.message.toString()}")
            return bitmap
        }
    }

    private fun getInitials(name: String): String {
        val names = name.split(" ")
        val initials = StringBuilder()

        if (names.isNotEmpty()) {
            initials.append(names[0][0].uppercaseChar())

            if (names.size > 1) {
                initials.append(names.last()[0].uppercaseChar())
            } else if (names[0].length > 1) {
                initials.append(names[0][1].uppercaseChar())
            }
        }

        return initials.toString()
    }

    companion object{
        private const val TAG = "UserAvatar"
    }
}
