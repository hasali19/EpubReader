package uk.co.hasali.epubreader.readers

import uk.co.hasali.epubreader.entities.EpubContentType
import uk.co.hasali.epubreader.refentities.EpubBookRef
import uk.co.hasali.epubreader.refentities.EpubByteContentFileRef
import uk.co.hasali.epubreader.refentities.EpubContentRef
import uk.co.hasali.epubreader.refentities.EpubTextContentFileRef

internal object ContentReader {

    @JvmStatic
    fun parseContentMap(bookRef: EpubBookRef): EpubContentRef {
        val result = EpubContentRef().apply {
            html = mutableMapOf()
            css = mutableMapOf()
            images = mutableMapOf()
            fonts = mutableMapOf()
            allFiles = mutableMapOf()
        }
        for (manifestItem in bookRef.schema!!.epubPackage!!.manifest!!) {
            val fileName = manifestItem.href!!
            val contentMimeType = manifestItem.mediaType!!
            val contentType = getContentTypeByContentMimeType(contentMimeType)
            when (contentType) {
                EpubContentType.XHTML_1_1,
                EpubContentType.CSS,
                EpubContentType.OEB1_DOCUMENT,
                EpubContentType.OEB1_CSS,
                EpubContentType.XML,
                EpubContentType.DTBOOK,
                EpubContentType.DTBOOK_NCX -> {
                    val epubTextContentFile = EpubTextContentFileRef(bookRef).apply {
                        this.fileName = fileName
                        this.contentMimeType = contentMimeType
                        this.contentType = contentType
                    }

                    when (contentType) {
                        EpubContentType.XHTML_1_1 -> result.html!![fileName] = epubTextContentFile
                        EpubContentType.CSS -> result.css!![fileName] = epubTextContentFile
                    }

                    result.allFiles!![fileName] = epubTextContentFile
                }

                else -> {
                    val epubByteContentFile = EpubByteContentFileRef(bookRef).apply {
                        this.fileName = fileName
                        this.contentMimeType = contentMimeType
                        this.contentType = contentType
                    }

                    when (contentType) {
                        EpubContentType.IMAGE_GIF,
                        EpubContentType.IMAGE_JPEG,
                        EpubContentType.IMAGE_PNG,
                        EpubContentType.IMAGE_SVG -> {
                            result.images!![fileName] = epubByteContentFile
                        }

                        EpubContentType.FONT_TRUETYPE,
                        EpubContentType.FONT_OPENTYPE -> {
                            result.fonts!![fileName] = epubByteContentFile
                        }
                    }

                    result.allFiles!![fileName] = epubByteContentFile
                }
            }
        }
        return result
    }

    @JvmStatic
    private fun getContentTypeByContentMimeType(contentMimeType: String): EpubContentType =
            when (contentMimeType.toLowerCase()) {
                "application/xhtml+xml" -> EpubContentType.XHTML_1_1
                "application/x-dtbook+xml" -> EpubContentType.DTBOOK
                "application/x-dtbncx+xml" -> EpubContentType.DTBOOK_NCX
                "text/x-oeb1-document" -> EpubContentType.OEB1_DOCUMENT
                "application/xml" -> EpubContentType.XML
                "text/css" -> EpubContentType.CSS
                "text/x-oeb1-css" -> EpubContentType.OEB1_CSS
                "image/gif" -> EpubContentType.IMAGE_GIF
                "image/jpeg" -> EpubContentType.IMAGE_JPEG
                "image/png" -> EpubContentType.IMAGE_PNG
                "image/svg+xml" -> EpubContentType.IMAGE_SVG
                "font/truetype" -> EpubContentType.FONT_TRUETYPE
                "font/opentype" -> EpubContentType.FONT_OPENTYPE
                "application/vnd.ms-opentype" -> EpubContentType.FONT_OPENTYPE
                else -> EpubContentType.OTHER
            }
}
