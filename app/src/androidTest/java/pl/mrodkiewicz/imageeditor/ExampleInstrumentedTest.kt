package pl.mrodkiewicz.imageeditor

import androidx.renderscript.Matrix3f
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

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
    fun checkMatrixPercentage(){
        var input = Matrix3f(
            floatArrayOf(.393f, .349f, .272f,
                .769f, .686f, .534f,
                .189f, .168f, .131f)
        )
    }
}