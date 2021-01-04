package pl.mrodkiewicz.imageeditor.helpers

import ar.com.hjg.pngj.ImageInfo
import ar.com.hjg.pngj.ImageLineInt
import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.PngWriter
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour
import java.io.File
import java.util.*

// source code https://github.com/leonbloy/pngj/blob/master/src/test/java/ar/com/hjg/pngj/samples/SampleTileImage.java
object SampleTileImage {
    fun doTiling(tiles: Array<String?>, dest: String?, nTilesX: Int) {
        val ntiles = tiles.size
        val nTilesY = (ntiles + nTilesX - 1) / nTilesX // integer ceil
        val imi1: ImageInfo
        val imi2: ImageInfo // 1:small tile 2:big image
        val pngr = PngReader(File(tiles[0]))
        imi1 = pngr.imgInfo
        val readers = arrayOfNulls<PngReader>(nTilesX)
        imi2 = ImageInfo(
            imi1.cols * nTilesX, imi1.rows * nTilesY, imi1.bitDepth, imi1.alpha, imi1.greyscale,
            imi1.indexed
        )
        val pngw = PngWriter(File(dest), imi2, true)
        // copy palette and transparency if necessary (more chunks?)
        pngw.copyChunksFrom(
            pngr.chunksList,
            ChunkCopyBehaviour.COPY_PALETTE or ChunkCopyBehaviour.COPY_TRANSPARENCY
        )
        pngr.end() // close, we'll reopen it again soon
        val line2 = ImageLineInt(imi2)
        var row2 = 0
        for (ty in 0 until nTilesY) {
            val nTilesXcur = if (ty < nTilesY - 1) nTilesX else ntiles - (nTilesY - 1) * nTilesX
            Arrays.fill(line2.scanline, 0)
            for (tx in 0 until nTilesXcur) { // open serveral readers
                readers[tx] = PngReader(File(tiles[tx + ty * nTilesX]))
                readers[tx]!!.setChunkLoadBehaviour(ChunkLoadBehaviour.LOAD_CHUNK_NEVER)
                if (readers[tx]!!.imgInfo != imi1) throw RuntimeException("different tile ? " + readers[tx]!!.imgInfo)
            }
            var row1 = 0
            while (row1 < imi1.rows) {
                for (tx in 0 until nTilesXcur) {
                    val line1 = readers[tx]!!.readRow(row1) as ImageLineInt // read line
                    System.arraycopy(
                        line1.scanline, 0, line2.scanline, line1.scanline.size * tx,
                        line1.scanline.size
                    )
                }
                pngw.writeRow(line2, row2) // write to full image
                row1++
                row2++
            }
            for (tx in 0 until nTilesXcur) readers[tx]!!.end() // close readers
        }
        pngw.end() // close writer
    }
}