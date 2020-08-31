package pl.mrodkiewicz.imageeditor

import org.junit.After
import org.junit.Test
import kotlin.math.round
import kotlin.random.Random
import kotlin.random.nextInt

class ExtensionsTest {
    @Test
    fun testExtensionsHSVHue() {
        var value = Random.nextInt(0..100).toFloat()
        assert(value.toHSVHue().fromHSVHue().toFloat().equals(value))
    }

    @Test
    fun testExtensionsHSVSaturation() {
        var value = Random.nextInt(0..100).toFloat()
        assert(value.toHSVSaturation().fromHSVSaturation().toFloat().equals(value))
    }

    @Test
    fun testExtensionsHSVValue() {
        var value = Random.nextInt(0..100).toFloat()
        assert((value.toHSVValue().fromHSVValue()).toFloat().equals(value))
    }

    @Test
    fun testExtensionsRGB() {
        var value = Random.nextInt(0..100).toFloat()
        assert(value.toRGB().fromRGB().toFloat().equals(value))
    }

}