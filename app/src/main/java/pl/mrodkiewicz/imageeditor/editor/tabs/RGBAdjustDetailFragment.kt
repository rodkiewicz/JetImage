package pl.mrodkiewicz.imageeditor.editor.tabs

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_base_adjust.*
import kotlinx.coroutines.InternalCoroutinesApi
import pl.mrodkiewicz.imageeditor.data.Filter
import kotlin.math.roundToInt

@AndroidEntryPoint
class RGBAdjustDetailFragment : BaseAdjustDetailFragment(){
    override fun syncSliders(filter: Filter) {
        base_seekBar_one.value = filter.red.toFloat()
        base_seekBar_two.value = filter.green.toFloat()
        base_seekBar_three.value = filter.blue.toFloat()
    }

    override fun handleSliderOneChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(red = value.roundToInt())
    }

    override fun handleSliderTwoChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(green = value.roundToInt())
    }

    override fun handleSliderThreeChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(blue = value.roundToInt())
    }
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        base_seekBar_one_title.text = "red"
        base_seekBar_two_title.text = "green"
        base_seekBar_three_title.text = "blue"
        base_seekBar_one.valueTo = 255F
        base_seekBar_two.valueTo = 255F
        base_seekBar_three.valueTo = 255F
        setup()
    }

}