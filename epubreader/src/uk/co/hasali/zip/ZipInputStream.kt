package uk.co.hasali.zip

import java.io.InputStream
import java.util.zip.ZipInputStream

internal class ZipInputStream(inputStream: InputStream) : IZipFile {

    private val mZipInputStream = ZipInputStream(inputStream)

    override fun getEntry(name: String?): IZipEntry? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        mZipInputStream.close()
    }
}
