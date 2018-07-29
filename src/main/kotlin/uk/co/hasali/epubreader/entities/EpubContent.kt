package uk.co.hasali.epubreader.entities

import uk.co.hasali.epubreader.entities.EpubByteContentFile
import uk.co.hasali.epubreader.entities.EpubContentFile
import uk.co.hasali.epubreader.entities.EpubTextContentFile

class EpubContent {
    var html: MutableMap<String, EpubTextContentFile>? = null
    var css: MutableMap<String, EpubTextContentFile>? = null
    var images: MutableMap<String, EpubByteContentFile>? = null
    var fonts: MutableMap<String, EpubByteContentFile>? = null
    var allFiles: MutableMap<String, EpubContentFile>? = null
}
