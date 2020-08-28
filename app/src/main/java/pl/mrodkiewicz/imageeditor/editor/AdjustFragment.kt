package pl.mrodkiewicz.imageeditor.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_adjust.*
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.editor.tabs.RGBAdjustDetailFragment
import pl.mrodkiewicz.imageeditor.editor.tabs.HSVAdjustDetailFragment


class AdjustFragment : Fragment(R.layout.fragment_adjust) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager_adjust.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> HSVAdjustDetailFragment()
                    else -> RGBAdjustDetailFragment()
                }
            }
        }
        TabLayoutMediator(tablayout_adjust, viewpager_adjust){ tab, index ->
            when(index){
                0 -> tab.text = "HSV"
                else -> tab.text = "RGB"
            }}.attach()
    }
}