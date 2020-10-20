package pl.mrodkiewicz.imageeditor.helpers

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun createPictureUri(
    context: Context,
    folder: String
): Uri? {
    var currentPhotoPath = ""
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File =
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/" + folder)
    storageDir.mkdir()
    val file = File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir/* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
    return FileProvider.getUriForFile(
        context,
        "rodkiewicz.carjournal.fileprovider",
        file
    )
}

