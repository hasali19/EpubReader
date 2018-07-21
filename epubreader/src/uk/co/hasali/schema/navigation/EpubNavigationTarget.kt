package uk.co.hasali.schema.navigation

class EpubNavigationTarget {
    var id: String? = null
    var className: String? = null
    var value: String? = null
    var playOrder: String? = null
    var navigationLabels: MutableList<EpubNavigationLabel>? = null
    var content: EpubNavigationContent? = null
}
