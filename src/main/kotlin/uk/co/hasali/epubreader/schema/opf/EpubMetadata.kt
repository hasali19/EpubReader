package uk.co.hasali.epubreader.schema.opf

class EpubMetadata {
    var titles: MutableList<String>? = null
    var creators: MutableList<EpubMetadataCreator>? = null
    var subjects: MutableList<String>? = null
    var description: String? = null
    var publishers: MutableList<String>? = null
    var contributors: MutableList<EpubMetadataContributor>? = null
    var dates: MutableList<EpubMetadataDate>? = null
    var types: MutableList<String>? = null
    var formats: MutableList<String>? = null
    var identifiers: MutableList<EpubMetadataIdentifier>? = null
    var sources: MutableList<String>? = null
    var languages: MutableList<String>? = null
    var relations: MutableList<String>? = null
    var coverages: MutableList<String>? = null
    var rights: MutableList<String>? = null
    var metaItems: MutableList<EpubMetadataMeta>? = null
}
