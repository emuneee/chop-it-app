package co.mainmethod.chop.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import co.mainmethod.chop.R
import android.view.MotionEvent
import timber.log.Timber
import android.graphics.DashPathEffect




/**
 * Created by evan on 10/31/17.
 */
class WaveformView : View {

    companion object {
        private const val MAX_FREQUENCY = 25_000
        private const val WAVEFORM_RESOLUTION = 12
        private const val LINE_WIDTH = WAVEFORM_RESOLUTION / 2
    }

    var frequencySamples: Array<Double>? = null

        set(value) {
            field = value
            invalidate()
        }

    private val selectorsPaint: Paint = Paint()
    private val progressPaint: Paint = Paint()
    private val boundsPaint: Paint = Paint()
    private val waveformPaint: Paint = Paint()
    private val selectedBoxPaint: Paint = Paint()
    private val unselectedBoxPaint: Paint = Paint()
    private var selectionStart: Float = -1f
    private var selectionEnd: Float = -1f

    var waveformListener: WaveformListener? = null

    var progressMs: Long = -1L
        set(value) {
            field = value
            invalidate()
        }

    var durationMs: Long = -1L
        set (value) {
            field = value
            selectionStart = 0f
            selectionEnd = measuredWidth.toFloat()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        waveformPaint.style = Paint.Style.STROKE
        waveformPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        waveformPaint.strokeWidth = 3f
        waveformPaint.isAntiAlias = true

        selectorsPaint.style = Paint.Style.STROKE
        selectorsPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        selectorsPaint.strokeWidth = 5f

        progressPaint.style = Paint.Style.STROKE
        progressPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        progressPaint.strokeWidth = 5f

        boundsPaint.style = Paint.Style.STROKE
        boundsPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        boundsPaint.strokeWidth = 10f
        boundsPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 5f)

        selectedBoxPaint.color = ContextCompat.getColor(context, R.color.white)
        unselectedBoxPaint.color = ContextCompat.getColor(context, R.color.trans_black)
    }

    fun clearFrequencySamples() {
        frequencySamples = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Timber.d("down")
            }
            MotionEvent.ACTION_MOVE -> {
                Timber.d("move")
                val distanceToStart = Math.abs(selectionStart - event.x)
                val distanceToEnd = Math.abs(selectionEnd - event.x)

                if (distanceToStart < distanceToEnd && event.x >= 0) {
                    selectionStart = event.x
                    waveformListener?.onStartUpdated(getTimestampAtPixel(selectionStart))
                } else if (distanceToStart > distanceToEnd && event.x <= measuredWidth) {
                    selectionEnd = event.x
                    waveformListener?.onEndUpdated(getTimestampAtPixel(selectionEnd))
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Timber.d("up")
            }
        }
        return true
    }

    private fun getTimestampAtPixel(pixel: Float): Long {
        return ((pixel / measuredWidth) * durationMs).toLong()
    }

    private fun getPixelAtTimestamp(timestampMs: Long): Float {
        return ((timestampMs.toFloat() / durationMs.toFloat()) * measuredWidth)
    }

    /**
     * Repaints the view's surface.
     *
     * @param canvas the [Canvas] object on which to draw
     */
    private fun drawWaveform(canvas: Canvas) {

        if (frequencySamples == null) {
            return
        }
        val width = width.toFloat()
        val height = height.toFloat()
        val centerY = height / 2
        val frequencyArr = ArrayList<Double>(WAVEFORM_RESOLUTION)

        for (i in 0 until width.toInt()) {

            if (i > 0 && i % WAVEFORM_RESOLUTION == 0) {
                // draw the frequency average for the collection frequencies
                val frequency = frequencyArr.average()
                frequencyArr.clear()
                val scaledHeight = (frequency / MAX_FREQUENCY) * height
                val lineHeight = scaledHeight / 2
                val startY = centerY - lineHeight
                val endY = centerY + lineHeight

                for (j in 0 until LINE_WIDTH) {
                    canvas.drawLine(i.toFloat() + j, startY.toFloat(), i.toFloat() + j,
                            endY.toFloat(), waveformPaint)
                }
            } else {
                // collect frequencies for the average
                val freqIndex = (frequencySamples!!.size * (i / width)).toInt()
                frequencyArr.add(frequencySamples!![freqIndex])
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (selectionStart == -1f) {
            selectionStart = 0f
        }

        if (selectionEnd == -1f) {
            selectionEnd = measuredWidth.toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {

        // draw background
        canvas?.drawColor(Color.WHITE)

        drawWaveform(canvas!!)

        // draw the selection boxes
        canvas.drawRect(0f, 0f, selectionStart, measuredHeight.toFloat(), unselectedBoxPaint)
        canvas.drawRect(selectionEnd, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), unselectedBoxPaint)

        // draw the selection lines
        canvas.drawLine(selectionStart, 0f, selectionEnd, 0f, boundsPaint)
        canvas.drawLine(selectionStart, measuredHeight.toFloat(), selectionEnd, measuredHeight.toFloat(), boundsPaint)
        canvas.drawLine(selectionStart, 0f, selectionStart, measuredHeight.toFloat(), selectorsPaint)
        canvas.drawLine(selectionEnd, 0f, selectionEnd, measuredHeight.toFloat(), selectorsPaint)

        // draw the progress line
        if (progressMs > -1L) {
            val progressX = getPixelAtTimestamp(progressMs)
            canvas.drawLine(progressX, 0f, progressX, measuredHeight.toFloat(), progressPaint)
        }
    }

    interface WaveformListener {
        fun onStartUpdated(value: Long)
        fun onEndUpdated(value: Long)
    }
}