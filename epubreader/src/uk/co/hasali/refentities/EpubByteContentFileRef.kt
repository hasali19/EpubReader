package uk.co.hasali.refentities

class EpubByteContentFileRef(epubBookRef: EpubBookRef) : EpubContentFileRef(epubBookRef) {
    fun readContent(): ByteArray {
        return readContentAsBytes()
    }
}
