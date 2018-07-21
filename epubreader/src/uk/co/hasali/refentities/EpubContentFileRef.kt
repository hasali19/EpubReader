package uk.co.hasali.refentities

import uk.co.hasali.entities.EpubContentType
import uk.co.hasali.utils.ZipPathUtils
import java.io.InputStream
import java.nio.charset.Charset
import java.util.zip.ZipEntry

abstract class EpubContentFileRef internal constructor(private val epubBookRef: EpubBookRef) {

    var fileName: String? = null
    var contentType: EpubContentType? = null
    var contentMimeType: String? = null

    fun readContentAsBytes(): ByteArray = contentStream.use { stream ->
        stream.readBytes()
    }

    fun readContentAsText(): String = contentStream.use { stream ->
        stream.readBytes().toString(Charset.forName("utf-8"))
    }

    val contentStream: InputStream
        get() = openContentStream(contentFileEntry)

    private val contentFileEntry: ZipEntry
        get() {
            val contentFilePath = ZipPathUtils.combine(epubBookRef.schema?.contentDirectoryPath, fileName)
            val contentFileEntry = epubBookRef.epubFile.getEntry(contentFilePath)
                    ?: throw Exception(String.format("EPUB parsing error: file %s not found in archive.", contentFilePath))
            if (contentFileEntry.size > Integer.MAX_VALUE) {
                throw Exception(String.format("EPUB parsing error: file %s is bigger than 2 Gb.", contentFilePath))
            }
            return contentFileEntry
        }

    private fun openContentStream(contentFileEntry: ZipEntry): InputStream {
        return epubBookRef.epubFile.getInputStream(contentFileEntry)
    }
}
