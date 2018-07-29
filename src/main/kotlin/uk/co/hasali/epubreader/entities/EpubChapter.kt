package uk.co.hasali.epubreader.entities

class EpubChapter {

    var title: String? = null
    var contentFileName: String? = null
    var anchor: String? = null
    var htmlContent: String? = null
    var subChapters: MutableList<EpubChapter>? = null

    override fun toString(): String {
        return "Title: $title, Subchapter count: ${subChapters?.size}"
    }
}
