package pl.mrodkiewicz.imageeditor

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.LayoutDirection
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.marginBottom
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private lateinit var sheetBehavior : BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomSheet()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_image -> {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                launchAskForPermissionThenImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomSheet() {
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
                if(it != null){
                        Glide.with(this).load(it).into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                val bitmap = resource.toBitmap()
                                GlobalScope.launch(Dispatchers.Main) {
                                    var newBitmap = loadSnowFilterAsync(bitmap)
                                    withContext(Dispatchers.Main) {
                                        imageView.setImageBitmap(newBitmap)
                                    }
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                        })

                } else {
                    snackbar("popsulo sie").show()
                }

            }

    private suspend fun loadSnowFilterAsync(originalBitmap: Bitmap): Bitmap =
        withContext(Dispatchers.Default) {
            originalBitmap.applyFilter(Filter(200, 200, 200))
        }
}
