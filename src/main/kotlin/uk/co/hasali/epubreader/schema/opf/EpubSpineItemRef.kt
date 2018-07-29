package uk.co.hasali.epubreader.schema.opf

class EpubSpineItemRef {

    var idref: String? = null
    var isLinear: Boolean = false

    override fun toString(): String {
        return "IdRef: $idref"
    }
}
