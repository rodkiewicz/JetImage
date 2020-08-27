package pl.mrodkiewicz.imageeditor.editor.tabs

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_base_adjust.*
import kotlinx.coroutines.InternalCoroutinesApi
import pl.mrodkiewicz.imageeditor.data.SECTION

class GreenAdjustDetailFragment : BaseAdjustDetailFragment(){
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSection(SECTION.GREEN)
    }
}