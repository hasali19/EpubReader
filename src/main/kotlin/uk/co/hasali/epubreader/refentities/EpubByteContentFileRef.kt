package uk.co.hasali.epubreader.refentities

class EpubByteContentFileRef(epubBookRef: EpubBookRef) : EpubContentFileRef(epubBookRef) {
    fun readContent(): ByteArray {
        return readContentAsBytes()
    }
}
