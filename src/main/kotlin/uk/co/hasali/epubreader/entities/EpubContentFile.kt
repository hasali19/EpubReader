package uk.co.hasali.epubreader.entities

import uk.co.hasali.epubreader.entities.EpubContentType

abstract class EpubContentFile {
    var fileName: String? = null
    var contentType: EpubContentType? = null
    var contentMimeType: String? = null
}
