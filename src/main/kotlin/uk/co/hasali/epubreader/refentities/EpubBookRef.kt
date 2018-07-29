package uk.co.hasali.epubreader.refentities

import uk.co.hasali.epubreader.entities.EpubSchema
import uk.co.hasali.epubreader.readers.BookCoverReader
import uk.co.hasali.epubreader.readers.ChapterReader
import uk.co.hasali.epubreader.zip.IZipFile

class EpubBookRef(val epubFile: IZipFile) : AutoCloseable {

    private var isClosed: Boolean = false

    var filePath: String? = null
    var title: String? = null
    var author: String? = null
    var authorList: MutableList<String>? = null
    var schema: EpubSchema? = null
    var content: EpubContentRef? = null

    val chapters: MutableList<EpubChapterRef>
        get() = ChapterReader.getChapters(this)

    init {
        isClosed = false
    }

    fun readCover(): ByteArray? {
        return BookCoverReader.readBookCover(this)
    }

    @Throws(Exception::class)
    override fun close() {
        if (!isClosed) {
            epubFile.close()
            isClosed = true
        }
    }
}
