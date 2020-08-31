package pl.mrodkiewicz.imageeditor.editor.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_base_adjust.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.afterValueChangedFlow
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.editor.EditorViewModel


const val DEBOUNCE = 25L

enum class MODE {
    RGB, HSV
}

abstract class BaseAdjustDetailFragment : Fragment(R.layout.fragment_base_adjust) {
    val editorViewModel: EditorViewModel by activityViewModels()

    @FlowPreview
    @InternalCoroutinesApi
    fun setup() {
        editorViewModel.filter.observe(viewLifecycleOwner, Observer {
            syncSliders(it)
        })

        lifecycleScope.launch {
            base_seekBar_one.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                handleSliderOneChange(it)?.let {
                        it -> editorViewModel.updateFilter(it)
                }
            }
        }
        lifecycleScope.launch {
            base_seekBar_two.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                handleSliderTwoChange(it)?.let { it -> editorViewModel.updateFilter(it)}
            }
        }
        lifecycleScope.launch {
            base_seekBar_three.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                handleSliderThreeChange(it)?.let { it -> editorViewModel.updateFilter(it)}
            }
        }
    }

    abstract fun syncSliders(filter: Filter)
    abstract fun handleSliderOneChange(value: Float): Filter?
    abstract fun handleSliderTwoChange(value: Float): Filter?
    abstract fun handleSliderThreeChange(value: Float): Filter?
}