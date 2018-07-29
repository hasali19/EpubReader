package uk.co.hasali.epubreader.schema.navigation

class EpubNavigationLabel {

    var text: String? = null

    override fun toString(): String {
        return if (text == null) "null" else text!!
    }
}
