package uk.co.hasali.epubreader.schema.navigation

class EpubNavigationList {
    var id: String? = null
    var className: String? = null
    var navigationLabels: MutableList<EpubNavigationLabel>? = null
    var navigationTargets: MutableList<EpubNavigationTarget>? = null
}
