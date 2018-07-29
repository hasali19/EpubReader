package uk.co.hasali.epubreader.entities

class EpubBook {
    var filePath: String? = null
    var title: String? = null
    var author: String? = null
    var authorList: MutableList<String>? = null
    var schema: EpubSchema? = null
    var content: EpubContent? = null
    var coverImage: ByteArray? = null
    var chapters: MutableList<EpubChapter>? = null
}
