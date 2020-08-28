package pl.mrodkiewicz.imageeditor.editor.tabs

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_base_adjust.*
import kotlinx.coroutines.InternalCoroutinesApi
import pl.mrodkiewicz.imageeditor.data.Filter
import kotlin.math.roundToInt

@AndroidEntryPoint
class HSVAdjustDetailFragment : BaseAdjustDetailFragment(){
    override fun syncSliders(filter: Filter) {
        base_seekBar_one.value = filter.hue.toFloat()
        base_seekBar_two.value = filter.saturation.toFloat()
        base_seekBar_three.value = filter.value.toFloat()
    }

    override fun handleSliderOneChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(hue = value.roundToInt())
    }

    override fun handleSliderTwoChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(saturation = value.roundToInt())
    }

    override fun handleSliderThreeChange(value: Float): Filter? {
        return editorViewModel.filter.value?.copy(value = value.roundToInt())
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        base_seekBar_one_title.text = "hue"
        base_seekBar_two_title.text = "saturation"
        base_seekBar_three_title.text = "value"
        setup()
    }
}