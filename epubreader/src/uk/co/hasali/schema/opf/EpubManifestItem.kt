package uk.co.hasali.schema.opf

class EpubManifestItem {

    var id: String? = null
    var href: String? = null
    var mediaType: String? = null
    var requiredNamespace: String? = null
    var requiredModules: String? = null
    var fallback: String? = null
    var fallbackStyle: String? = null

    override fun toString(): String {
        return "Id: $id, Href: $href, MediaType: $mediaType"
    }
}
