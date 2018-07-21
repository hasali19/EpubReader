package uk.co.hasali.schema.navigation

class EpubNavigationPoint {

    var id: String? = null
    var className: String? = null
    var playOrder: String? = null
    var navigationLabels: MutableList<EpubNavigationLabel>? = null
    var content: EpubNavigationContent? = null
    var childNavigationPoints: MutableList<EpubNavigationPoint>? = null

    override fun toString(): String {
        return "Id: $id, Content.Source: ${content?.source}"
    }
}
