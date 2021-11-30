package pl.mrodkiewicz.imageeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.renderscript.Matrix3f
import androidx.renderscript.RenderScript
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.FilterType
import pl.mrodkiewicz.imageeditor.data.darkMatrix
import pl.mrodkiewicz.imageeditor.processor.BlurImageProcessor
import pl.mrodkiewicz.imageeditor.processor.ColorFilterMatrixImageProcessor
import java.io.InputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("pl.mrodkiewicz.imageeditor", appContext.packageName)
    }


    @Test
    fun checkBlurImageProcessor(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val renderScript = RenderScript.create(appContext)
        val imageProcessor = BlurImageProcessor(renderScript)
        val bitmap = BitmapFactory.decodeResource( InstrumentationRegistry.getInstrumentation().context.resources,pl.mrodkiewicz.imageeditor.test.R.drawable.abc)
        val filter = AdjustFilter(filterMatrix = FilterType.Blur(), value = 0)
        val outputBitmap = imageProcessor.loadBlur(bitmap,filter)
        assert(bitmap.sameAs(outputBitmap))
    }
}