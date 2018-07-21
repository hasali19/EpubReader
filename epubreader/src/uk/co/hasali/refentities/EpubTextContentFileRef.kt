package uk.co.hasali.refentities

class EpubTextContentFileRef(epubBookRef: EpubBookRef) : EpubContentFileRef(epubBookRef) {
    fun readContent(): String {
        return readContentAsText()
    }
}