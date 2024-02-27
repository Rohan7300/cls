package com.clebs.celerity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.graphics.Path
import com.clebs.celerity.DeductionAgreement.Companion.brush
import com.clebs.celerity.DeductionAgreement.Companion.path

class DrawViewClass : View {
    private var params: ViewGroup.LayoutParams? = null

    companion object {
        var pathList = ArrayList<android.graphics.Path>()
        var currentBrush = Color.BLACK
    }

    constructor(context: Context) : this(context, null) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initial()
    }

    private fun initial() {
        brush.isAntiAlias = true
        brush.color = currentBrush
        brush.strokeJoin = Paint.Join.ROUND
        brush.style = Paint.Style.STROKE
        brush.strokeWidth = 8f

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path = Path()
                path.moveTo(x, y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                pathList.add(path)
            }

            else -> return false
        }
        postInvalidate()
        return false
    }

    override fun onDraw(canvas: Canvas) {
        for (i in pathList.indices) {
            canvas.drawPath(pathList[i], brush)
            invalidate()
        }
        super.onDraw(canvas)
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun clearSignature() {
        pathList.clear()
        invalidate()
    }


}

