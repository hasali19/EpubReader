package uk.co.hasali.epubreader.schema.navigation

class EpubNavigationContent {

    var id: String? = null
    var source: String? = null

    override fun toString(): String {
        return "Source: $source"
    }
}
