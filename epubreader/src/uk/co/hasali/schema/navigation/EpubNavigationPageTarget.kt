package uk.co.hasali.schema.navigation

class EpubNavigationPageTarget {
    var id: String? = null
    var value: String? = null
    var type: EpubNavigationPageTargetType? = null
    var className: String? = null
    var playOrder: String? = null
    var navigationLabels: MutableList<EpubNavigationLabel>? = null
    var content: EpubNavigationContent? = null
}
