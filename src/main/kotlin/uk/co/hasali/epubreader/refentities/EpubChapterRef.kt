package uk.co.hasali.epubreader.refentities

class EpubChapterRef(private val epubTextContentFileRef: EpubTextContentFileRef) {

    var title: String? = null
    var contentFileName: String? = null
    var anchor: String? = null
    var subChapters: MutableList<EpubChapterRef>? = null

    fun readHtmlContent(): String {
        return epubTextContentFileRef.readContentAsText()
    }

    override fun toString(): String {
        return "Title: $title, Subchapter count: ${subChapters?.size}"
    }
}
