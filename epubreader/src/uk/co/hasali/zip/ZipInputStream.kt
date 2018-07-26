package uk.co.hasali.zip

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

internal class ZipInputStream(inputStream: InputStream) : IZipFile {

    private val mZipInputStream = ZipInputStream(inputStream)
    private val mEntries: MutableMap<String, IZipEntry> = mutableMapOf()

    init {
        var entry = mZipInputStream.nextEntry
        while (entry != null) {
            mEntries[entry.name] = ZipInputStreamEntry(entry.size, mZipInputStream.readBytes())
            mZipInputStream.closeEntry()
            entry = mZipInputStream.nextEntry
        }
    }

    override fun getEntry(name: String?): IZipEntry? {
        return mEntries[name]
    }

    override fun close() {
        mZipInputStream.close()
    }

    private class ZipInputStreamEntry(override val size: Long, private val data: ByteArray) : IZipEntry {
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }
    }
}
