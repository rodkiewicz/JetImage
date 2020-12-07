package pl.mrodkiewicz.imageeditor.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun Bitmap.saveImage(
    context: Context,
    folder: String,
    filename: String,
): Uri? {

    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File =
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/" + folder)
    storageDir.mkdir()
    var file = File.createTempFile(
        "PNG_${filename}_${timeStamp}_", /* prefix */
        ".png", /* suffix */
        storageDir/* directory */
    )

    var uri = FileProvider.getUriForFile(
        context,
        "pl.mrodkiewicz.imageeditor.fileprovider",
        file
    )
    try {
        val bmpOut = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, 100, bmpOut)
        bmpOut.close()
    } catch (e: NullPointerException) {
        return null
    }
    return uri
}

fun addImageToGallery(context: Context, filePath: String) {
    val values = ContentValues()

    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    values.put(
        MediaStore.Images.Media.MIME_TYPE,
        MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filePath))
    )
    values.put(MediaStore.MediaColumns.DATA, filePath)

    context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}

