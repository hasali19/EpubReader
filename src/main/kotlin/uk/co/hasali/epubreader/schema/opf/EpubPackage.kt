package uk.co.hasali.epubreader.schema.opf

class EpubPackage {
    var epubVersion: EpubVersion? = null
    var metadata: EpubMetadata? = null
    var manifest: EpubManifest? = null
    var spine: EpubSpine? = null
    var guide: EpubGuide? = null
}
