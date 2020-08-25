package pl.mrodkiewicz.imageeditor.editor
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.foundation.Text
//import androidx.compose.material.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.Recomposer
//import androidx.compose.ui.platform.setContent
//import androidx.fragment.app.Fragment
//import pl.mrodkiewicz.imageeditor.R
//
//class AdjustDetailFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val fragmentView = inflater.inflate(R.layout.fragment_adjust, container, false)
//
//        (fragmentView as ViewGroup).setContent(Recomposer.current()) {
//            Hello("Jetpack Compose")
//        }
//        return fragmentView
//    }
//
//    @Composable
//    fun Hello(name: String) = MaterialTheme {
//            Text(name)
//    }
//
//}