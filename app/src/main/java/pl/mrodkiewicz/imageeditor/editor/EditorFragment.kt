package pl.mrodkiewicz.imageeditor.editor

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.editor_sheet.*
import kotlinx.android.synthetic.main.fragment_editor.*
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.checkIfGranted
import pl.mrodkiewicz.imageeditor.snackbar
import pl.mrodkiewicz.imageeditor.toast


@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor) {
    private val editorViewModel: EditorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        initView()
        setHasOptionsMenu(true)
    }
    private fun setUpNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment_editor) as NavHostFragment

        navHostFragment?.let{
            bottom_nav.setupWithNavController(navHostFragment.navController)
        }

    }
    private fun initView() {
        editorViewModel.bitmap.observe(viewLifecycleOwner, Observer {
            it?.let {
                imageView.setImageBitmap(it)
            }
        })
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
                        editorViewModel.setBitmap(resource.toBitmap())
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
                launchAskForPermissionThenImage()
                true
            }
            R.id.reset_filter -> {
                editorViewModel.resetFilter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}