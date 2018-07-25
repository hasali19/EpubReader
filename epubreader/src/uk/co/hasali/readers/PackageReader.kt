package uk.co.hasali.readers

import org.w3c.dom.Element
import uk.co.hasali.schema.opf.*
import uk.co.hasali.utils.XmlUtils
import uk.co.hasali.zip.IZipFile
import java.net.URL
import java.util.zip.ZipFile

private const val NS_OPF = "http://www.idpf.org/2007/opf"

internal object PackageReader {

    @JvmStatic
    fun readPackage(epubFile: IZipFile, rootFilePath: String): EpubPackage {
        val rootFileEntry = epubFile.getEntry(rootFilePath)
                ?: throw Exception("EPUB parsing error: root file not found in archive.")
        val containerDocument = rootFileEntry.getInputStream().use { stream ->
            XmlUtils.loadDocument(stream)
        }

        val packageNode = containerDocument.getElementsByTagName("package").item(0) as Element
        val result = EpubPackage()

        val epubVersionValue = packageNode.getAttribute("version")
        result.epubVersion = when (epubVersionValue) {
            "2.0" -> EpubVersion.EPUB_2
            "3.0" -> EpubVersion.EPUB_3
            else -> throw Exception("Unsupported EPUB version: $epubVersionValue.")
        }

        val metadataNode = packageNode.getElementsByTagName("metadata").item(0) as Element?
                ?: throw Exception("EPUB parsing error: metadata not found in the package.")
        val metadata = readMetadata(metadataNode, result.epubVersion!!)
        result.metadata = metadata

        val manifestNode = packageNode.getElementsByTagName("manifest").item(0) as Element?
            ?: throw Exception("EPUB parsing error: manifest not found in the package.")
        val manifest = readManifest(manifestNode)
        result.manifest = manifest

        val spineNode = packageNode.getElementsByTagName("spine").item(0) as Element?
                ?: throw Exception("EPUB parsing error: spine not found in the package.")
        val spine = readSpine(spineNode)
        result.spine = spine

        val guideNode = packageNode.getElementsByTagName("guide").item(0) as Element?
        if (guideNode != null) {
            val guide = readGuide(guideNode)
            result.guide = guide
        }

        return result
    }

    @JvmStatic
    private fun readMetadata(metadataNode: Element, epubVersion: EpubVersion): EpubMetadata {
        val result = EpubMetadata().apply {
            this.titles = mutableListOf()
            this.creators = mutableListOf()
            this.subjects = mutableListOf()
            this.publishers = mutableListOf()
            this.contributors = mutableListOf()
            this.dates = mutableListOf()
            this.types = mutableListOf()
            this.formats = mutableListOf()
            this.identifiers = mutableListOf()
            this.sources = mutableListOf()
            this.languages = mutableListOf()
            this.relations = mutableListOf()
            this.coverages = mutableListOf()
            this.rights = mutableListOf()
            this.metaItems = mutableListOf()
        }

        val metadataItemNodes = metadataNode.childNodes
        for (i in 0 until metadataItemNodes.length) {
            val metadataItemNode = metadataItemNodes.item(i)
            if (metadataItemNode is Element) {
                val innerText = metadataItemNode.textContent
                when (metadataItemNode.localName.toLowerCase()) {
                    "title" -> result.titles!!.add(innerText)
                    "creator" -> {
                        val creator = readMetadataCreator(metadataItemNode)
                        result.creators!!.add(creator)
                    }
                    "subject" -> result.subjects!!.add(innerText)
                    "description" -> result.description = innerText
                    "publisher" -> result.publishers!!.add(innerText)
                    "contributor" -> {
                        val contributor = readMetadataContributor(metadataItemNode)
                        result.contributors!!.add(contributor)
                    }
                    "date" -> {
                        val date = readMetadataDate(metadataItemNode)
                        result.dates!!.add(date)
                    }
                    "type" -> result.types!!.add(innerText)
                    "format" -> result.formats!!.add(innerText)
                    "identifier" -> {
                        val identifier = readMetadataIdentifier(metadataItemNode)
                        result.identifiers!!.add(identifier)
                    }
                    "source" -> result.sources!!.add(innerText)
                    "language" -> result.languages!!.add(innerText)
                    "relation" -> result.relations!!.add(innerText)
                    "coverage" -> result.coverages!!.add(innerText)
                    "rights" -> result.rights!!.add(innerText)
                    "meta" -> {
                        val meta = readMetadataMeta(metadataItemNode)
                        result.metaItems!!.add(meta)
                    }
                }
            }
        }

        return result
    }

    @JvmStatic
    private fun readMetadataCreator(metadataCreatorNode: Element): EpubMetadataCreator {
        val result = EpubMetadataCreator()
        val metadataCreatorNodeAttributes = metadataCreatorNode.attributes
        for (i in 0 until metadataCreatorNodeAttributes.length) {
            val metadataCreatorNodeAttribute = metadataCreatorNodeAttributes.item(i)
            val attributeValue = metadataCreatorNodeAttribute.textContent
            when (metadataCreatorNodeAttribute.localName.toLowerCase()) {
                "role" -> result.role = attributeValue
                "file-as" -> result.fileAs = attributeValue
            }
        }
        result.creator = metadataCreatorNode.textContent
        return result
    }

    @JvmStatic
    private fun readMetadataContributor(metadataContributorNode: Element): EpubMetadataContributor {
        val result = EpubMetadataContributor()
        val metadataContributorNodeAttributes = metadataContributorNode.attributes
        for (i in 0 until metadataContributorNodeAttributes.length) {
            val metadataContributorNodeAttribute = metadataContributorNodeAttributes.item(i)
            val attributeValue = metadataContributorNodeAttribute.textContent
            when (metadataContributorNodeAttribute.localName.toLowerCase()) {
                "role" -> result.role = attributeValue
                "file-as" -> result.fileAs = attributeValue
            }
        }
        result.contributor = metadataContributorNode.textContent
        return result
    }

    @JvmStatic
    private fun readMetadataDate(metadataDateNode: Element): EpubMetadataDate {
        val result = EpubMetadataDate()
        val eventAttribute = metadataDateNode.getAttributeNS(NS_OPF, "event")
        if (!eventAttribute.isNullOrBlank()) {
            result.event = eventAttribute
        }
        result.date = metadataDateNode.textContent
        return result
    }

    @JvmStatic
    private fun readMetadataIdentifier(metadataIdentifierNode: Element): EpubMetadataIdentifier {
        val result = EpubMetadataIdentifier()
        val metadataIdentifierNodeAttributes = metadataIdentifierNode.attributes
        for (i in 0 until metadataIdentifierNodeAttributes.length) {
            val metadataIdentifierNodeAttribute = metadataIdentifierNodeAttributes.item(i)
            val attributeValue = metadataIdentifierNodeAttribute.textContent
            when (metadataIdentifierNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "scheme" -> result.scheme = attributeValue
            }
        }
        result.identifier = metadataIdentifierNode.textContent
        return result
    }

    @JvmStatic
    private fun readMetadataMeta(metadataMetaNode: Element): EpubMetadataMeta {
        val result = EpubMetadataMeta()
        val metadataMetaNodeAttributes = metadataMetaNode.attributes
        for (i in 0 until metadataMetaNodeAttributes.length) {
            val metadataMetaNodeAttribute = metadataMetaNodeAttributes.item(i)
            val attributeValue = metadataMetaNodeAttribute.textContent
            when (metadataMetaNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "name" -> result.name = attributeValue
                "content" -> result.content = attributeValue
                "refines" -> result.refines = attributeValue
                "property" -> result.property = attributeValue
                "scheme" -> result.scheme = attributeValue
            }
        }
        if (result.content == null) {
            result.content = metadataMetaNode.textContent
        }
        return result
    }

    @JvmStatic
    private fun readManifest(manifestNode: Element): EpubManifest {
        val result = EpubManifest()
        val manifestItemNodes = manifestNode.childNodes
        for (i in 0 until manifestItemNodes.length) {
            val manifestItemNode = manifestItemNodes.item(i)
            if (manifestItemNode is Element) {
                if (manifestItemNode.localName.toLowerCase() == "item") {
                    val manifestItem = EpubManifestItem()
                    val manifestItemNodeAttributes = manifestItemNode.attributes
                    for (j in 0 until manifestItemNodeAttributes.length) {
                        val manifestItemNodeAttribute = manifestItemNodeAttributes.item(j)
                        val attributeValue = manifestItemNodeAttribute.textContent
                        when (manifestItemNodeAttribute.localName.toLowerCase()) {
                            "id" -> manifestItem.id = attributeValue
                            "href" -> manifestItem.href = attributeValue
                            "media-type" -> manifestItem.mediaType = attributeValue
                            "required-namespace" -> manifestItem.requiredNamespace = attributeValue
                            "required-modules" -> manifestItem.requiredModules = attributeValue
                            "fallback" -> manifestItem.fallback = attributeValue
                            "fallback-style" -> manifestItem.fallbackStyle = attributeValue
                        }
                    }

                    if (manifestItem.id.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB manifest: item ID is missing")
                    }

                    if (manifestItem.href.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB manifest: item href is missing")
                    }

                    if (manifestItem.mediaType.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB manifest: item media type is missing")
                    }

                    result.add(manifestItem)
                }
            }
        }
        return result
    }

    @JvmStatic
    private fun readSpine(spineNode: Element): EpubSpine {
        val result = EpubSpine()
        val tocAttribute = spineNode.getAttribute("toc")
        if (tocAttribute.isNullOrBlank()) {
            throw Exception("Incorrect EPUB spine: TOC is missing")
        }
        result.toc = tocAttribute
        val spineItemNodes = spineNode.childNodes
        for (i in 0 until spineItemNodes.length) {
            val spineItemNode = spineItemNodes.item(i)
            if (spineItemNode is Element) {
                if (spineItemNode.localName.toLowerCase() == "itemref") {
                    val spineItemRef = EpubSpineItemRef()
                    val idRefAttribute = spineItemNode.getAttribute("idref")
                    if (idRefAttribute.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB spine: item ID ref is missing")
                    }
                    spineItemRef.idref = idRefAttribute
                    val linearAttribute = spineItemNode.getAttribute("linear")
                    spineItemRef.isLinear = linearAttribute.isNullOrBlank() || linearAttribute.toLowerCase() == "no"
                    result.add(spineItemRef)
                }
            }
        }
        return result
    }

    @JvmStatic
    private fun readGuide(guideNode: Element): EpubGuide {
        val result = EpubGuide()
        val guideReferenceNodes = guideNode.childNodes
        for (i in 0 until guideReferenceNodes.length) {
            val guideReferenceNode = guideReferenceNodes.item(i)
            if (guideReferenceNode is Element) {
                if (guideReferenceNode.localName.toLowerCase() == "reference") {
                    val guideReference = EpubGuideReference()
                    val guideReferenceNodeAttributes = guideReferenceNode.attributes
                    for (j in 0 until guideReferenceNodeAttributes.length) {
                        val guideReferenceNodeAttribute = guideReferenceNodeAttributes.item(j)
                        val attributeValue = guideReferenceNodeAttribute.textContent
                        when (guideReferenceNodeAttribute.localName.toLowerCase()) {
                            "type" -> guideReference.type = attributeValue
                            "title" -> guideReference.title = attributeValue
                            "href" -> guideReference.href = attributeValue
                        }
                    }

                    if (guideReference.type.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB guide: item type is missing")
                    }

                    if (guideReference.href.isNullOrBlank()) {
                        throw Exception("Incorrect EPUB guide: item href is missing")
                    }

                    result.add(guideReference)
                }
            }
        }
        return result
    }
}
