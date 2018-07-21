package uk.co.hasali.entities

import uk.co.hasali.schema.navigation.EpubNavigation
import uk.co.hasali.schema.opf.EpubPackage

class EpubSchema {
    var epubPackage: EpubPackage? = null
    var navigation: EpubNavigation? = null
    var contentDirectoryPath: String? = null
}
