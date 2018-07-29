package uk.co.hasali.epubreader.schema.opf

import uk.co.hasali.epubreader.schema.opf.EpubSpineItemRef

class EpubSpine : ArrayList<EpubSpineItemRef>() {
    var toc: String? = null
}
