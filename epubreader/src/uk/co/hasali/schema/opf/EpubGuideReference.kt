package uk.co.hasali.schema.opf

class EpubGuideReference {

    var type: String? = null
    var title: String? = null
    var href: String? = null

    override fun toString(): String {
        return "Type: $type, Href: $href"
    }
}
