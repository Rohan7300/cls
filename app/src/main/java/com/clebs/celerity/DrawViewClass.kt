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
import android.graphics.PathMeasure
import android.util.Log
import android.widget.Toast
import com.clebs.celerity.DeductionAgreement.Companion.brush
import com.clebs.celerity.DeductionAgreement.Companion.path

class DrawViewClass : View {
    private var params: ViewGroup.LayoutParams? = null

    companion object {
        private var touchMoved = false
        var pathList = ArrayList<android.graphics.Path>()
        var currentBrush = Color.BLACK
        fun isSignatureValid(): Boolean {
            val averagePathLength = pathList.size
            val threshold = 25f // Set your desired threshold value here
            return averagePathLength >= threshold
            Log.e("moved", "isSignatureValid: "+ touchMoved )
        }

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
        brush.style = Paint.Style.STROKE
        brush.strokeWidth = 1f

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
                touchMoved = false
                  return true

            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                touchMoved = true
                    pathList.add(path)


            }
            else -> {

                return false
            }


        }
        postInvalidate()
        return false
    }

    override fun onDraw(canvas: Canvas) {
        for (i in pathList.indices) {
            canvas.drawPath(pathList[i], brush)
            invalidate()

        }
        Log.e("drawviewsizes", "onDraw: "+ pathList.size.toString() )
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

