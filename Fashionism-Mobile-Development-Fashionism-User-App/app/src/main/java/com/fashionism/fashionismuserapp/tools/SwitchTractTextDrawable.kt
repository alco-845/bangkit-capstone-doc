package com.fashionism.fashionismuserapp.tools

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import com.fashionism.fashionismuserapp.R

class SwitchTrackTextDrawable(
    context: Context,
    @StringRes leftTextId: Int,
    @StringRes rightTextId: Int
) : Drawable() {
    private val mContext: Context
    private val mLeftText: String
    private val mRightText: String
    private val mTextPaint: Paint

    init {
        mContext = context

        // Left text
        mLeftText = context.getString(leftTextId)
        mTextPaint = createTextPaint()

        // Right text
        mRightText = context.getString(rightTextId)
    }

    private fun createTextPaint(): Paint {
        val textPaint = Paint()
        textPaint.color = mContext.resources.getColor(R.color.white)
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        // Set textSize, typeface, etc, as you wish
        return textPaint
    }

//    override fun draw(canvas: Canvas) {
//        val textBounds = Rect()
//        mTextPaint.getTextBounds(mRightText, 0, mRightText.length, textBounds)
//
//        // The baseline for the text: centered, including the height of the text itself
//        val heightBaseline: Int = canvas.clipBounds.height() / 2 + textBounds.height() / 2
//
//        // This is one quarter of the full width, to measure the centers of the texts
//        val widthQuarter: Int = canvas.clipBounds.width() / 4
//        canvas.drawText(
//            mLeftText, 0, mLeftText.length,
//            widthQuarter, heightBaseline,
//            mTextPaint
//        )
//        canvas.drawText(
//            mRightText, 0, mRightText.length,
//            widthQuarter * 3, heightBaseline,
//            mTextPaint
//        )
//    }

    override fun draw(canvas: Canvas) {
        val textBounds = Rect()
        mTextPaint.getTextBounds(mRightText, 0, mRightText.length, textBounds)

        // The baseline for the text: centered, including the height of the text itself
        val heightBaseline = canvas.clipBounds.height() / 2 + textBounds.height() / 2

        // This is one quarter of the full width, to measure the centers of the texts
        val widthQuarter = canvas.clipBounds.width() / 4.toFloat()
        canvas.drawText(
            mLeftText,
            0,
            mLeftText.length,
            widthQuarter,
            heightBaseline.toFloat(),
            mTextPaint
        )
        canvas.drawText(
            mRightText,
            0,
            mRightText.length,
            widthQuarter * 3,
            heightBaseline.toFloat(),
            mTextPaint
        )
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}


//class SwitchTrackTextDrawable(
//    private val context: Context,
//    @StringRes private val leftTextId: Int,
//    @StringRes private val rightTextId: Int
//) : Drawable() {
//
//    private val textPaint: Paint = createTextPaint()
//
//    private val leftText: String = context.getString(leftTextId)
//
//    private val rightText: String = context.getString(rightTextId)
//
//    private fun createTextPaint(): Paint {
//        val textPaint = Paint()
//        textPaint.color = ContextCompat.getColor(context, android.R.color.white)
//        textPaint.isAntiAlias = true
//        textPaint.style = Paint.Style.FILL
//        textPaint.textAlign = Paint.Align.CENTER
//        // Set textSize, typeface, etc, as you wish
//        return textPaint
//    }
//
//    override fun draw(canvas: Canvas) {
//        val textBounds = Rect()
//        textPaint.getTextBounds(rightText, 0, rightText.length, textBounds)
//
//        // The baseline for the text: centered, including the height of the text itself
//        val heightBaseline = canvas.clipBounds.height() / 2 + textBounds.height() / 2
//
//        // This is one quarter of the full width, to measure the centers of the texts
//        val widthQuarter = canvas.clipBounds.width() / 4.toFloat()
//        canvas.drawText(
//            leftText,
//            0,
//            leftText.length,
//            widthQuarter,
//            heightBaseline.toFloat(),
//            textPaint
//        )
//        canvas.drawText(
//            rightText,
//            0,
//            rightText.length,
//            widthQuarter * 3,
//            heightBaseline.toFloat(),
//            textPaint
//        )
//    }
//
//    override fun setAlpha(alpha: Int) {
//    }
//
//    override fun setColorFilter(colorFilter: ColorFilter?) {
//    }
//
//    override fun getOpacity(): Int {
//        return PixelFormat.TRANSLUCENT
//    }
//}
