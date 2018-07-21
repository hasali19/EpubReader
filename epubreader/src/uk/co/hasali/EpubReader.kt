package uk.co.hasali

import uk.co.hasali.entities.*
import uk.co.hasali.readers.ContentReader
import uk.co.hasali.readers.SchemaReader
import uk.co.hasali.refentities.*
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipFile

object EpubReader {

    @JvmStatic
    fun openBook(filePath: String): EpubBookRef {
        val file = File(filePath)
        return openBook(file)
    }

    @JvmStatic
    fun openBook(file: File): EpubBookRef {
        if (!file.exists()) {
            throw FileNotFoundException("Specified epub file not found: ${file.path}")
        }
        return openBook(getZipFile(file))
    }

    @JvmStatic
    fun readBook(filePath: String): EpubBook {
        val epubBookRef = openBook(filePath)
        return readBook(epubBookRef)
    }

    @JvmStatic
    fun readBook(file: File): EpubBook {
        val epubBookRef = openBook(file)
        return readBook(epubBookRef)
    }

    @JvmStatic
    private fun openBook(zipFile: ZipFile, filePath: String? = null): EpubBookRef {
        var result: EpubBookRef? = null
        return try {
            result = EpubBookRef(zipFile).apply {
                this.filePath = filePath
                this.schema = SchemaReader.readSchema(zipFile)
                this.title = this.schema?.epubPackage?.metadata?.titles?.firstOrNull() ?: ""
                this.authorList = this.schema?.epubPackage?.metadata?.creators?.mapTo(mutableListOf()) { it.creator!! }
                this.author = this.authorList?.joinToString(separator = ", ")
                this.content = ContentReader.parseContentMap(this)
            }
            result
        } catch (ex: Exception) {
            result?.close()
            throw ex
        }
    }

    @JvmStatic
    private fun readBook(epubBookRef: EpubBookRef): EpubBook {
        val result = EpubBook()
        epubBookRef.use { epubBookRef ->
            result.filePath = epubBookRef.filePath
            result.schema = epubBookRef.schema
            result.title = epubBookRef.title
            result.authorList = epubBookRef.authorList
            result.author = epubBookRef.author
            result.content = readContent(epubBookRef.content!!)
            result.coverImage = epubBookRef.readCover()
            result.chapters = readChapters(epubBookRef.chapters)
        }
        return result
    }

    @JvmStatic
    private fun getZipFile(filePath: String): ZipFile {
        return ZipFile(filePath)
    }

    @JvmStatic
    private fun getZipFile(file: File): ZipFile {
        return ZipFile(file, ZipFile.OPEN_READ)
    }

    @JvmStatic
    private fun readContent(contentRef: EpubContentRef): EpubContent {
        val result = EpubContent().apply {
            this.html = readTextContentFiles(contentRef.html!!)
            this.css = readTextContentFiles(contentRef.css!!)
            this.images = readByteContentFiles(contentRef.images!!)
            this.fonts = readByteContentFiles(contentRef.fonts!!)
            this.allFiles = mutableMapOf()
        }

        val textContentFiles = mutableMapOf<String, EpubTextContentFile>()
        textContentFiles.putAll(result.html!!)
        textContentFiles.putAll(result.css!!)
        for (textContentFile in textContentFiles) {
            result.allFiles!![textContentFile.key] = textContentFile.value
        }

        val byteContentFiles = mutableMapOf<String, EpubByteContentFile>()
        byteContentFiles.putAll(result.images!!)
        byteContentFiles.putAll(result.fonts!!)
        for (byteContentFile in byteContentFiles) {
            result.allFiles!![byteContentFile.key] = byteContentFile.value
        }

        for (contentFileRef in contentRef.allFiles!!) {
            if (!result.allFiles!!.containsKey(contentFileRef.key)) {
                result.allFiles!![contentFileRef.key] = readByteContentFile(contentFileRef.value)
            }
        }

        return result
    }

    @JvmStatic
    private fun readTextContentFiles(textContentFileRefs: Map<String, EpubTextContentFileRef>): MutableMap<String, EpubTextContentFile> {
        val result = mutableMapOf<String, EpubTextContentFile>()
        for (textContentFileRef in textContentFileRefs) {
            val textContentFile = EpubTextContentFile().apply {
                fileName = textContentFileRef.value.fileName
                contentType = textContentFileRef.value.contentType
                contentMimeType = textContentFileRef.value.contentMimeType
            }
            textContentFile.content = textContentFileRef.value.readContentAsText()
            result[textContentFileRef.key] = textContentFile
        }
        return result
    }

    @JvmStatic
    private fun readByteContentFiles(byteContentFileRefs: Map<String, EpubByteContentFileRef>): MutableMap<String, EpubByteContentFile> {
        val result = mutableMapOf<String, EpubByteContentFile>()
        for (byteContentFileRef in byteContentFileRefs) {
            result[byteContentFileRef.key] = readByteContentFile(byteContentFileRef.value)
        }
        return result
    }

    @JvmStatic
    private fun readByteContentFile(contentFileRef: EpubContentFileRef): EpubByteContentFile {
        val result = EpubByteContentFile().apply {
            fileName = contentFileRef.fileName
            contentType = contentFileRef.contentType
            contentMimeType = contentFileRef.contentMimeType
        }
        result.content = contentFileRef.readContentAsBytes()
        return result
    }

    @JvmStatic
    private fun readChapters(chapterRefs: MutableList<EpubChapterRef>): MutableList<EpubChapter> {
        val result = mutableListOf<EpubChapter>()
        for (chapterRef in chapterRefs) {
            val chapter = EpubChapter().apply {
                title = chapterRef.title
                contentFileName = chapterRef.contentFileName
                anchor = chapterRef.anchor
                htmlContent = chapterRef.readHtmlContent()
                subChapters = readChapters(chapterRef.subChapters!!)
            }
            result.add(chapter)
        }
        return result
    }
}