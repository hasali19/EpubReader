package uk.co.hasali.epubreader.schema.navigation

import uk.co.hasali.epubreader.schema.navigation.EpubNavigationContent
import uk.co.hasali.epubreader.schema.navigation.EpubNavigationLabel

class EpubNavigationTarget {
    var id: String? = null
    var className: String? = null
    var value: String? = null
    var playOrder: String? = null
    var navigationLabels: MutableList<EpubNavigationLabel>? = null
    var content: EpubNavigationContent? = null
}
