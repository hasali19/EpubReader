package uk.co.hasali.epubreader.refentities

class EpubTextContentFileRef(epubBookRef: EpubBookRef) : EpubContentFileRef(epubBookRef) {
    fun readContent(): String {
        return readContentAsText()
    }
}
