package pl.mrodkiewicz.imageeditor.editor

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_editor.*
import kotlinx.android.synthetic.main.fragment_editor.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.afterValueChangedFlow
import pl.mrodkiewicz.imageeditor.checkIfGranted
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.VALUE_UPDATED
import pl.mrodkiewicz.imageeditor.snackbar
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor) {
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private val editorViewModel: EditorViewModel by viewModels()
    private lateinit var bitmap: Bitmap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setHasOptionsMenu(true)
    }

    private fun initView() {
        editorViewModel.bitmap.observe(viewLifecycleOwner, Observer {
            it?.let {
                imageView.setImageBitmap(it)
            }
        })
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val params: ViewGroup.LayoutParams = imageView.getLayoutParams()
                val params2: ViewGroup.LayoutParams = main_activity.getLayoutParams()
                params.height = bottomSheet.y.roundToInt() - params2.height
                main_activity.requestLayout()
            }

        })
        sheetBehavior.isDraggable = true
        sheetBehavior.peekHeight = 100
        lifecycleScope.launch {
            var red = seekBar_red.afterValueChangedFlow().debounce(250).collect {
                editorViewModel.updateFilter(it * 255 / 100, VALUE_UPDATED.RED)
            }
        }
        lifecycleScope.launch {
            var green = seekBar_green.afterValueChangedFlow().debounce(250).collect {
                editorViewModel.updateFilter(it * 255 / 100, VALUE_UPDATED.GREEN)
            }
        }
        lifecycleScope.launch {
            var blue = seekBar_blue.afterValueChangedFlow().debounce(250).collect {
                editorViewModel.updateFilter(it * 255 / 100, VALUE_UPDATED.BLUE)
            }
        }

    }

    private fun launchAskForPermissionThenImage() {
        askForPermissionThenImage.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private val askForPermissionThenImage =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.checkIfGranted()) {
                askForImage.launch("image/*")
            } else {
                snackbar("daj nosz mi uprawnienia").setAction(
                    "RETRY",
                    { launchAskForPermissionThenImage() }).show()
            }
        }


    private val askForImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { it ->
            println(it)
            if (it != null) {
                Glide.with(this).load(it).into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        bitmap = resource.toBitmap()
                        editorViewModel.setBitmap(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })

            } else {
                snackbar("popsulo sie").show()
            }

        }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_image -> {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                launchAskForPermissionThenImage()
                true
            }
            R.id.center_image -> {
                imageView.centerImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}