package pl.mrodkiewicz.imageeditor.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_adjust.*
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.editor.tabs.AlphaAdjustDetailFragment
import pl.mrodkiewicz.imageeditor.editor.tabs.BlueAdjustDetailFragment
import pl.mrodkiewicz.imageeditor.editor.tabs.GreenAdjustDetailFragment
import pl.mrodkiewicz.imageeditor.editor.tabs.RedAdjustDetailFragment


class AdjustFragment : Fragment(R.layout.fragment_adjust) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager_adjust.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> RedAdjustDetailFragment()
                    1 -> GreenAdjustDetailFragment()
                    2 -> BlueAdjustDetailFragment()
                    else -> AlphaAdjustDetailFragment()
                }
            }
        }
        TabLayoutMediator(tablayout_adjust, viewpager_adjust){ tab, index ->
            when(index){
                0 -> tab.text = "RED"
                1 -> tab.text = "GREEN"
                2 -> tab.text = "BLUE"
                else -> tab.text = "ALPHA"
            }}.attach()
    }
}