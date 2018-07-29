package uk.co.hasali.epubreader.entities

import uk.co.hasali.epubreader.schema.navigation.EpubNavigation
import uk.co.hasali.epubreader.schema.opf.EpubPackage

class EpubSchema {
    var epubPackage: EpubPackage? = null
    var navigation: EpubNavigation? = null
    var contentDirectoryPath: String? = null
}
