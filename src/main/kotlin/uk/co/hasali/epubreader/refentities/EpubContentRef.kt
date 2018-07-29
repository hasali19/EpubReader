package uk.co.hasali.epubreader.refentities

class EpubContentRef {
    var html: MutableMap<String, EpubTextContentFileRef>? = null
    var css: MutableMap<String, EpubTextContentFileRef>? = null
    var images: MutableMap<String, EpubByteContentFileRef>? = null
    var fonts: MutableMap<String, EpubByteContentFileRef>? = null
    var allFiles: MutableMap<String, EpubContentFileRef>? = null
}
