package pl.mrodkiewicz.imageeditor

import org.junit.Test
import pl.mrodkiewicz.imageeditor.data.SECTION
import pl.mrodkiewicz.imageeditor.data.VALUE_UPDATED

class FilterUtilsTest {
     @Test
     fun testFilterUtilsIndex0(){
         var filterUtils = FilterUtils()

         assert(filterUtils.convertToMatrixIndex(section = SECTION.RED, updated_value = VALUE_UPDATED.RED)==0)
     }
    @Test
    fun testFilterUtilsIndex1(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.RED, updated_value = VALUE_UPDATED.GREEN)==1)
    }
    @Test
    fun testFilterUtilsIndex2(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.RED, updated_value = VALUE_UPDATED.BLUE)==2)
    }

    @Test
    fun testFilterUtilsIndex5(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.GREEN, updated_value = VALUE_UPDATED.RED)==5)
    }
    @Test
    fun testFilterUtilsIndex6(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.GREEN, updated_value = VALUE_UPDATED.GREEN)==6)
    }
    @Test
    fun testFilterUtilsIndex7(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.GREEN, updated_value = VALUE_UPDATED.BLUE)==7)
    }

    @Test
    fun testFilterUtilsIndex10(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.BLUE, updated_value = VALUE_UPDATED.RED)==10)
    }
    @Test
    fun testFilterUtilsIndex11(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.BLUE, updated_value = VALUE_UPDATED.GREEN)==11)
    }
    @Test
    fun testFilterUtilsIndex12(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.BLUE, updated_value = VALUE_UPDATED.BLUE)==12)
    }

    @Test
    fun testFilterUtilsIndex15(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.ALPHA, updated_value = VALUE_UPDATED.RED)==15)
    }
    @Test
    fun testFilterUtilsIndex16(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.ALPHA, updated_value = VALUE_UPDATED.GREEN)==16)
    }
    @Test
    fun testFilterUtilsIndex17(){
        var filterUtils = FilterUtils()
        assert(filterUtils.convertToMatrixIndex(section = SECTION.ALPHA, updated_value = VALUE_UPDATED.BLUE)==17)
    }

 }