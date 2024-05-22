package com.clebs.celerity.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.speech.tts.TextToSpeech
import android.util.AttributeSet
import android.view.View
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import kotlin.math.max

class OverlayViewTwo (context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()

    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()
    var textToSpeech: TextToSpeech? = null
    var drawableText= String()
    private var cardetected = false
    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

//        for (result in results) {
//            if (result.categories[0].label.equals("car")) {
//                cardetected = true
//            } else {
//                cardetected = false
//            }
//        }
//        if (cardetected) {
//            boxPaint.color = Color.RED
//        } else {
//            boxPaint.color = Color.GREEN
//        }
        boxPaint.strokeWidth = 5F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor
            for (results in results) {

                if (results.categories[0].label.equals("person") || results.categories[0].label.equals("face")){
                    boxPaint.color= Color.GREEN


//                    textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
//
//                        // setting the language to the default phone language.
//                        val ttsLang = textToSpeech!!.setLanguage(Locale.getDefault())
//                        val text =
//                            "Vehicle Detected"
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//
//                            val voices: Set<Voice> = textToSpeech!!.getVoices()
//                            val voiceList: List<Voice> = ArrayList(voices)
//                            val selectedVoice = voiceList[8] // Change to the desired voice index
//
//                            textToSpeech!!.setVoice(selectedVoice)
//                            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
//                        } else {
//                            val voices: Set<Voice> = textToSpeech!!.getVoices()
//                            val voiceList: List<Voice> = ArrayList(voices)
//                            val selectedVoice = voiceList[8] // Change to the desired voice index
//
//                            textToSpeech!!.setVoice(selectedVoice)
//                            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//                        }
//
//                        // check if the language is supportable.
//                        if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
////                            showToast("error", this@HomeActivity)
//                        }
//
//
//                    })
                }
                else{
                    boxPaint.color= Color.RED
                }
            }
            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)


            canvas.drawRect(drawableRect, boxPaint)

            // Create text to display alongside detected objects

            if (result.categories[0].label.equals("person")){
               drawableText ="Face detected"
            }
            else{
               drawableText=   result.categories[0].label

            }


            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }

    }

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults

        // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
        // the size that the captured images will be displayed.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }


    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}