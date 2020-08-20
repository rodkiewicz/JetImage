package pl.mrodkiewicz.imageeditor.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_adjust.*
import pl.mrodkiewicz.imageeditor.R


class AdjustTabFragment : Fragment(R.layout.fragment_adjust) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewpager_adjust.adapter = object : FragmentStateAdapter(this){
//            override fun getItemCount(): Int = 10
//            override fun createFragment(position: Int): Fragment {
//                return when(position){
//                    1 -> AdjustDetailFragment()
//                    else -> AdjustDetailFragment()
//                }
//            }
//
//        }
    }
}