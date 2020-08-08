package pl.mrodkiewicz.imageeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import timber.log.Timber
import kotlin.math.abs


// thanks https://www.freshbytelabs.com/2018/12/how-to-create-zoomable-imageview-in.html
class ZoomableImageView : AppCompatImageView {
    private var mode = MODE.NONE
    private val last = PointF()
    private val start = PointF()
    private val minScale = 0.8f
    private var maxScale = 10f
    private lateinit var matrix_array: FloatArray
    private lateinit var matrix_image: Matrix
    private var redundantXSpace = 0f
    private var redundantYSpace = 0f
    private var resetPosition = false
    private var saveScale = 1f
    private var right = 0f
    private var bottom = 0f
    private var originalBitmapWidth = 0f
    private var originalBitmapHeight = 0f
    private lateinit var mScaleDetector: ScaleGestureDetector

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        super.setClickable(true)
        Timber.d("HEHEH INTI")
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        matrix_array = FloatArray(9)
        matrix_image = Matrix()
        imageMatrix = matrix_image
        scaleType = ScaleType.MATRIX
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val bmHeight = bmHeight
        val bmWidth = bmWidth
        val width: Float = measuredWidth.toFloat()
        val height: Float = measuredHeight.toFloat()

        val scale = if (width > height) height / bmHeight else width / bmWidth
        matrix_image.setScale(scale, scale)
        saveScale = 1f
        originalBitmapWidth = scale * bmWidth
        originalBitmapHeight = scale * bmHeight


        redundantYSpace = height - originalBitmapHeight
        redundantXSpace = width - originalBitmapWidth
        matrix_image.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
        setImageMatrix(matrix_image)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (saveScale > 0.9f) {
            mScaleDetector.onTouchEvent(event)
        }
        matrix_image.getValues(matrix_array)
        val x = matrix_array[Matrix.MTRANS_X]
        val y = matrix_array[Matrix.MTRANS_Y]
        val curr = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                last[event.x] = event.y
                start.set(last)
                mode = MODE.DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                last[event.x] = event.y
                start.set(last)
                mode = MODE.ZOOM
            }
            MotionEvent.ACTION_MOVE ->
                if (mode == MODE.ZOOM || mode == MODE.DRAG && saveScale > minScale) {
                    var deltaX = curr.x - last.x
                    var deltaY = curr.y - last.y

                    val scaleWidth = Math.round(originalBitmapWidth * saveScale)
                        .toFloat()
                    val scaleHeight = Math.round(originalBitmapHeight * saveScale)
                        .toFloat()
                    var limitX = false
                    var limitY = false

                    if (scaleWidth < getWidth() && scaleHeight < getHeight()) {
                        // don't do anything
                    } else if (scaleWidth < getWidth()) {
                        deltaX = 0f
                        limitY = true
                    } else if (scaleHeight < getHeight()) {
                        deltaY = 0f
                        limitX = true
                    } else if (curr.y > scaleHeight) {
                        limitX = true
                        limitY = false
                    } else {
                        limitX = true
                        limitY = true
                    }
                    if (limitY) {
                        if (y + deltaY > 0) {
                            deltaY = -y
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom)
                        }
                    }
                    if (limitX) {
                        if (x + deltaX > 0) {
                            deltaX = -x
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right)
                        }
                    }
                    matrix_image.postTranslate(deltaX, deltaY);
                    last[curr.x] = curr.y
                }
            MotionEvent.ACTION_UP -> {
                mode = MODE.NONE
                val xDiff = abs(curr.x - start.x).toInt()
                val yDiff = abs(curr.y - start.y).toInt()
                if (xDiff < 3 && yDiff < 3) performClick()
                if (resetPosition) centerImage()
            }
            MotionEvent.ACTION_POINTER_UP -> mode = MODE.NONE
        }
        setImageMatrix(matrix_image)
        invalidate()
        return true
    }

    fun centerImage() {
        val bmHeight = bmHeight
        val bmWidth = bmWidth
        val width: Float = measuredWidth.toFloat()
        val height: Float = measuredHeight.toFloat()
        val scale = if (width > height) height / bmHeight else width / bmWidth
        matrix_image.setScale(scale, scale)
        saveScale = 1f
        originalBitmapWidth = scale * bmWidth
        originalBitmapHeight = scale * bmHeight

        redundantYSpace = height - originalBitmapHeight
        redundantXSpace = width - originalBitmapWidth
        matrix_image.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
        imageMatrix = matrix_image
        resetPosition = false
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = MODE.ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = saveScale * scaleFactor
            if (newScale < 1.0f) {
                resetPosition = true
                newScale == 1.0f
            }
            if (newScale < maxScale && newScale > minScale) {
                saveScale = newScale
                val width = getWidth().toFloat()
                val height = getHeight().toFloat()
                right = originalBitmapWidth * saveScale - width
                bottom = originalBitmapHeight * saveScale - height
                val scaledBitmapWidth = originalBitmapWidth * saveScale
                val scaledBitmapHeight = originalBitmapHeight * saveScale
                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    matrix_image.postScale(scaleFactor, scaleFactor, width / 2, height / 2)
                } else {
                    matrix_image.postScale(
                        scaleFactor,
                        scaleFactor,
                        detector.focusX,
                        detector.focusY
                    )
                }
            }
            return true
        }
    }



    private val bmWidth: Int
        get() {
            val drawable: Drawable? = drawable
            return drawable?.intrinsicWidth ?: 0
        }
    private val bmHeight: Int
        get() {
            val drawable: Drawable? = drawable
            return drawable?.intrinsicHeight ?: 0
        }

}

enum class MODE{
    NONE,
    DRAG,
    ZOOM,
    CLICK,

}