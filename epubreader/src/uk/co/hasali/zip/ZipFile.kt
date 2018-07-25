package uk.co.hasali.zip

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

internal class ZipFile(file: File) : IZipFile {

    private val mZipFile = ZipFile(file, ZipFile.OPEN_READ)

    override fun getEntry(name: String?): IZipEntry? {
        val entry = mZipFile.getEntry(name) ?: return null
        return ZipFileEntry(entry)
    }

    override fun close() {
        mZipFile.close()
    }

    private inner class ZipFileEntry(private val entry: ZipEntry) : IZipEntry {

        override val size: Long
            get() = entry.size

        override fun getInputStream() = mZipFile.getInputStream(entry)
    }
}
