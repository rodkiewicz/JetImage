package pl.mrodkiewicz.imageeditor.editor.tabs

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_base_adjust.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.afterValueChangedFlow
import pl.mrodkiewicz.imageeditor.data.SECTION
import pl.mrodkiewicz.imageeditor.data.VALUE_UPDATED
import pl.mrodkiewicz.imageeditor.editor.EditorViewModel
import pl.mrodkiewicz.imageeditor.toast
import kotlin.math.roundToInt


const val DEBOUNCE = 250L

@AndroidEntryPoint
open class BaseAdjustDetailFragment : Fragment(R.layout.fragment_base_adjust) {
    val editorViewModel: EditorViewModel by activityViewModels()

    @FlowPreview
    @InternalCoroutinesApi
    fun setupSection(section: SECTION) {
        syncSliders(section)
        lifecycleScope.launch {
            var red = base_seekBar_red.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                editorViewModel.updateFilter(it, section, VALUE_UPDATED.RED)
            }
        }
        lifecycleScope.launch {
            var green = base_seekBar_green.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                editorViewModel.updateFilter(it, section, VALUE_UPDATED.GREEN)
            }
        }
        lifecycleScope.launch {
            var blue = base_seekBar_blue.afterValueChangedFlow().debounce(DEBOUNCE).collect {
                editorViewModel.updateFilter(it, section, VALUE_UPDATED.BLUE)
            }
        }
        lifecycleScope.launch {
            editorViewModel.filters.receiveAsFlow().collect {
                toast("ELOELO o tej poze kazdy wypic moze ${it.toString()}")
                syncSliders(section)
            }
        }
    }
    fun syncSliders(section: SECTION){
        editorViewModel.getFilterValues(section).forEach {
            when (it.first) {
                VALUE_UPDATED.RED -> base_seekBar_red.value = it.second
                VALUE_UPDATED.GREEN -> base_seekBar_green.value = it.second
                VALUE_UPDATED.BLUE -> base_seekBar_blue.value = it.second
            }
        }
    }
}