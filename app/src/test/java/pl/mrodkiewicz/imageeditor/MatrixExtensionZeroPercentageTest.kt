package pl.mrodkiewicz.imageeditor

import androidx.renderscript.Matrix3f
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.mrodkiewicz.imageeditor.helpers.darkArray
import pl.mrodkiewicz.imageeditor.helpers.nonFilteredArray

class MatrixExtensionZeroPercentageTest {
    lateinit var matrix3f: Matrix3f
    var expectedArray : FloatArray = nonFilteredArray

    @Before
    fun prepareTest() {
        matrix3f = Matrix3f(darkArray)
        matrix3f.serPercentageForMatrix(0)
    }

    @Test
    fun checkValueAtIndex0(){
        var index = 0
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }

    @Test
    fun checkValueAtIndex1(){
        var index = 1
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex2(){
        var index = 2
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex3(){
        var index = 3
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex4(){
        var index = 4
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex5(){
        var index = 5
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex6(){
        var index = 6
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex7(){
        var index = 7
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @Test
    fun checkValueAtIndex8(){
        var index = 8
        assert(matrix3f.array[index]==expectedArray[index], {"${matrix3f.array[index]} is not equal to ${expectedArray[index]}"})
    }
    @After
    fun printArrays(){
        println("MatrixExtensionZeroPercentageTest")
        for(i in 0..8){
            print("[${expectedArray[i]}]")
            print(" [${matrix3f.array[i]}]")
            println()
        }
    }

}