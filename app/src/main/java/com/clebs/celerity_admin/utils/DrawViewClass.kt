package com.clebs.celerity_admin.utils

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
import com.clebs.celerity_admin.ui.VanHireReturnAgreementActivity.Companion.brush
import kotlin.math.hypot

class DrawViewClass : View {
    private var params: ViewGroup.LayoutParams? = null
    private var currentPath: Path? = null
    private var isDrawing = false
    private var isMoving = false
    private var totalPathLength = 0.0f
    private var lastX = 0.0f
    private var lastY = 0.0f
    private val minimumSignatureLength = 100.0f

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
        brush.style = Paint.Style.STROKE
        brush.strokeWidth = 2f

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
                currentPath = Path().apply {
                    moveTo(x, y)
                }
                isDrawing = true
                lastX = x
                lastY = y
                totalPathLength = 0.0f
                lastX = x
                lastY = y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(x, y)
                isMoving = true
                totalPathLength += calculateDistance(lastX, lastY, x, y)
                lastX = x
                lastY = y
                postInvalidate()
                isDrawing = true
            }
            MotionEvent.ACTION_UP -> {

                if (isDrawing&&isMoving&& totalPathLength >= minimumSignatureLength) {
                    currentPath?.let {
                        pathList.add(it)
                    }
                    //showToast("Sign length $totalPathLength", context)
                }else {
                   showToast("Signature is too short", context)
                }
                currentPath = null
                postInvalidate()
                isMoving = false
                isDrawing = false
            }
            else -> return false
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (path in pathList) {
            canvas.drawPath(path, brush)
        }
        currentPath?.let {
            canvas.drawPath(it, brush) // Draw the current path in real-time
        }
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun clearSignature() {
        pathList.clear()
        currentPath = null
        invalidate()
    }
    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return hypot((x2 - x1), (y2 - y1))
    }

}

