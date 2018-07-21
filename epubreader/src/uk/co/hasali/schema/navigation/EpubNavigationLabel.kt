package uk.co.hasali.schema.navigation

class EpubNavigationLabel {

    var text: String? = null

    override fun toString(): String {
        return if (text == null) "null" else text!!
    }
}
