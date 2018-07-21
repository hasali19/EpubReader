package uk.co.hasali.entities

class EpubContent {
    var html: MutableMap<String, EpubTextContentFile>? = null
    var css: MutableMap<String, EpubTextContentFile>? = null
    var images: MutableMap<String, EpubByteContentFile>? = null
    var fonts: MutableMap<String, EpubByteContentFile>? = null
    var allFiles: MutableMap<String, EpubContentFile>? = null
}
