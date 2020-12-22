package pl.mrodkiewicz.imageeditor.helpers

import android.R.attr.bitmap
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import ar.com.hjg.pngj.ImageInfo
import ar.com.hjg.pngj.ImageLineInt
import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.PngWriter
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour
import pl.mrodkiewicz.imageeditor.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt


fun Uri.loadBitmap(context: Context): Bitmap {
    var inputStream: InputStream? = context.contentResolver.openInputStream(this)
    val exif = inputStream?.let { it1 -> ExifInterface(it1) }
    var orientation = exif?.getAttributeInt(
        androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
    ) ?: 0
    val matrix = decodeExifOrientation(orientation)
    inputStream?.close()
    inputStream = context.contentResolver.openInputStream(this)
    val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
    return android.graphics.Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width,
        bitmap.height, matrix, true
    )
}

fun Bitmap.divideIntoTiles(parts: Int): MutableList<Bitmap> {
    var bitmapList = mutableListOf<Bitmap>()
    var w = width / parts
    var h = height / parts
    for (y in 0 until parts) {
        for (x in 0 until parts) {
            bitmapList.add(Bitmap.createBitmap(this, x * w, y * h, w, h))
        }
    }

    return bitmapList
}
fun MutableList<MutableList<Bitmap>>.combineTilesIntoBitmap(): Bitmap {
    var parts = sqrt(this.size.toDouble()).toInt()
    var width = this[0][0].width
    var height = this[0][0].height
    var outputBitmap = Bitmap.createBitmap(width * parts,height * parts,Bitmap.Config.ARGB_8888)
    var canvas = Canvas(outputBitmap)
    for (x in 0 until parts) {
        for (y in 0 until parts) {
            canvas.drawBitmap(this[x][y], (x * width).toFloat(), (y * height).toFloat(), null)
        }
    }
    return outputBitmap

}

fun List<String>.saveTiles(string: String){
    Timber.d("saveTiles ${this.toString()}")
    var tileSize = sqrt(this.size.toDouble()).toInt()
    var reader = PngReader(File(this[0])) // path to file
    var tileImageInfo  = reader.imgInfo
    var outputImageInfo = ImageInfo(this.size * tileSize, this.size * tileSize, tileImageInfo.bitDepth,tileImageInfo.alpha,tileImageInfo.greyscale,tileImageInfo.indexed)
    var readers = mutableListOf<PngReader>()
    var writer = PngWriter(File(string), outputImageInfo,true)
    writer.copyChunksFrom(reader.chunksList, ChunkCopyBehaviour.COPY_PALETTE or ChunkCopyBehaviour.COPY_TRANSPARENCY)
    reader.close()
    var line2 = ImageLineInt(outputImageInfo)
    var row2 = 0
    for (ty in 0 until tileSize)
    {
        val nTilesXcur = if (ty < tileSize - 1) tileSize else tileSize - (tileSize - 1) * tileSize
        Arrays.fill(line2.scanline, 0)
        Timber.d("line2 size ${line2.scanline.size} ${nTilesXcur} ")
        for (tx in 0 until nTilesXcur)
        { // open serveral readers
            readers.add(tx,PngReader(File(this[tx + ty * tileSize])))
            readers[tx].setChunkLoadBehaviour(ChunkLoadBehaviour.LOAD_CHUNK_NEVER)
            if (!readers[tx].imgInfo.equals(tileImageInfo))
                throw RuntimeException("different tile ? " + readers[tx].imgInfo)
        }
        var row1 = 0
        while (row1 < tileImageInfo.rows)
        {
            for (tx in 0 until nTilesXcur)
            {
                val line1 = readers[tx].readRow(row1) as ImageLineInt // read line
                System.arraycopy(line1.scanline, 0, line2.scanline, line1.scanline.size * tx,
                    line1.scanline.size)
            }
            writer.writeRow(line2, row2) // write to full image
            row1++
            row2++
        }
        for (tx in 0 until nTilesXcur)
            readers[tx].end() // close readers
    }
    writer.end()

}

fun createPictureUri(
    context: Context,
    folder: String,
    name: String,
): File {
    var currentPhotoPath = ""
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File =
        File(context.filesDir, "images")
    storageDir.mkdir()
    val file = File.createTempFile(
        "JPEG_${name}_${timeStamp}", /* prefix */
        ".jpg", /* suffix */
        storageDir/* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
    return file
}

fun Bitmap.saveImage(
    context: Context,
    folder: String,
    filename: String,
): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imagePath: File = File(context.filesDir, "images")
    imagePath.mkdir()
    var file = File.createTempFile(
        "PNG_${filename}_${timeStamp}_", /* prefix */
        ".PNG", /* suffix */
        imagePath/* directory */
    )

    var uri = FileProvider.getUriForFile(
        context,
        "pl.mrodkiewicz.imageeditor.provider",
        file
    )

    try {
        val bmpOut = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, 100, bmpOut)
        bmpOut.close()
    } catch (e: NullPointerException) {
    }
    return file.absolutePath
}

fun getUriForCameraPhoto(context: Context): Uri? {
    val imagePath: File = File(context.filesDir, "images")
    if (!imagePath.exists()) {
        imagePath.mkdirs()
    }
    val filename = "image.jpg"
    val file = File(imagePath, filename)
    return FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".provider", file
    )
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

