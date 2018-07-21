package uk.co.hasali.readers

import org.w3c.dom.Element
import uk.co.hasali.schema.navigation.*
import uk.co.hasali.schema.opf.EpubPackage
import uk.co.hasali.utils.XmlUtils
import uk.co.hasali.utils.ZipPathUtils
import java.util.zip.ZipFile

internal object NavigationReader {

    @JvmStatic
    fun readNavigation(epubFile: ZipFile, contentDirectoryPath: String, epubPackage: EpubPackage): EpubNavigation {
        val result = EpubNavigation()
        val tocId = epubPackage.spine?.toc
        if (tocId.isNullOrEmpty()) {
            throw Exception("EPUB parsing error: TOC ID is empty.")
        }

        val tocManifestItem = epubPackage.manifest?.firstOrNull { it.id.equals(tocId, true) }
                ?: throw Exception("EPUB parsing error: TOC item $tocId not found in EPUB manifest.")

        val tocFileEntryPath = ZipPathUtils.combine(contentDirectoryPath, tocManifestItem.href)
        val tocFileEntry = epubFile.getEntry(tocFileEntryPath)
                ?: throw Exception("EPUB parsing error: TOC file $tocFileEntryPath not found in archive.")
        if (tocFileEntry.size > Int.MAX_VALUE) {
            throw Exception("EPUB parsing error: TOC file $tocFileEntryPath is larger than 2 Gb.")
        }

        val containerDocument = epubFile.getInputStream(tocFileEntry).use { stream ->
            XmlUtils.loadDocument(stream)
        }

        val ncxNode = containerDocument.getElementsByTagName("ncx").item(0) as Element?
                ?: throw Exception("EPUB parsing error: TOC file does not contain ncx element.")

        val headNode = ncxNode.getElementsByTagName("head").item(0) as Element?
                ?: throw Exception("EPUB parsing error: TOC file does not contain head element.")
        val navigationHead = readNavigationHead(headNode)
        result.head = navigationHead

        val docTitleNode = ncxNode.getElementsByTagName("docTitle").item(0) as Element?
                ?: throw Exception("EPUB parsing error: TOC file does not contain docTitle element.")
        val navigationDocTitle = readNavigationDocTitle(docTitleNode)
        result.docTitle = navigationDocTitle

        result.docAuthors = mutableListOf()
        val docAuthorNodes = ncxNode.getElementsByTagName("docAuthor")
        for (i in 0 until docAuthorNodes.length) {
            val docAuthorNode = docAuthorNodes.item(i) as Element
            val navigationDocAuthor = readNavigationDocAuthor(docAuthorNode)
            result.docAuthors!!.add(navigationDocAuthor)
        }

        val navMapNode = ncxNode.getElementsByTagName("navMap").item(0) as Element?
                ?: throw Exception("EPUB parsing error: TOC file does not contain navMap element.")
        val navMap = readNavigationMap(navMapNode)
        result.navMap = navMap

        val pageListNode = ncxNode.getElementsByTagName("pageList").item(0) as Element?
        if (pageListNode != null) {
            val pageList = readNavigationPageList(pageListNode)
            result.pageList = pageList
        }

        result.navLists = mutableListOf()
        val navigationListNodes = ncxNode.getElementsByTagName("navList")
        for (i in 0 until navigationListNodes.length) {
            val navigationListNode = navigationListNodes.item(i) as Element
            val navigationList = readNavigationList(navigationListNode)
            result.navLists!!.add(navigationList)
        }

        return result
    }

    @JvmStatic
    private fun readNavigationHead(headNode: Element): EpubNavigationHead {
        val result = EpubNavigationHead()
        val metaNodes = headNode.getElementsByTagName("meta")
        for (i in 0 until metaNodes.length) {
            val metaNode = metaNodes.item(i) as Element
            val meta = EpubNavigationHeadMeta()
            for (j in 0 until metaNode.attributes.length) {
                val metaNodeAttribute = metaNode.attributes.item(j)
                val attributeValue = metaNodeAttribute.textContent
                when (metaNodeAttribute.localName.toLowerCase()) {
                    "name" -> meta.name = attributeValue
                    "content" -> meta.content = attributeValue
                    "scheme" -> meta.scheme = attributeValue
                }
            }
            if (meta.name.isNullOrBlank()) {
                throw Exception("Incorrect EPUB navigation meta: meta name is missing.")
            }
            if (meta.content == null) {
                throw Exception("Incorrect EPUB navigation meta: meta content is missing.")
            }
            result.add(meta)
        }
        return result
    }

    @JvmStatic
    private fun readNavigationDocTitle(docTitleNode: Element): EpubNavigationDocTitle {
        val result = EpubNavigationDocTitle()
        val textNodes = docTitleNode.getElementsByTagName("text")
        for (i in 0 until textNodes.length) {
            val textNode = textNodes.item(i) as Element
            result.add(textNode.textContent)
        }
        return result
    }

    @JvmStatic
    private fun readNavigationDocAuthor(docAuthorNode: Element): EpubNavigationDocAuthor {
        val result = EpubNavigationDocAuthor()
        val textNodes = docAuthorNode.getElementsByTagName("text")
        for (i in 0 until textNodes.length) {
            val textNode = textNodes.item(i) as Element
            result.add(textNode.textContent)
        }
        return result
    }

    @JvmStatic
    private fun readNavigationMap(navigationMapNode: Element): EpubNavigationMap {
        val result = EpubNavigationMap()
        val navigationPointNodes = navigationMapNode.getElementsByTagName("navPoint")
        for (i in 0 until navigationPointNodes.length) {
            val navigationPointNode = navigationPointNodes.item(i) as Element
            val navigationPoint = readNavigationPoint(navigationPointNode)
            result.add(navigationPoint)
        }
        return result
    }

    @JvmStatic
    private fun readNavigationPoint(navigationPointNode: Element): EpubNavigationPoint {
        val result = EpubNavigationPoint()
        val navigationPointNodeAttributes = navigationPointNode.attributes
        for (i in 0 until navigationPointNodeAttributes.length) {
            val navigationPointNodeAttribute = navigationPointNodeAttributes.item(i)
            val attributeValue = navigationPointNodeAttribute.textContent
            when (navigationPointNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "class" -> result.className = attributeValue
                "playorder" -> result.playOrder = attributeValue
            }
        }

        if (result.id.isNullOrBlank()) {
            throw Exception("Incorrect EPUB navigation point: point ID is missing.")
        }

        result.navigationLabels = mutableListOf()
        result.childNavigationPoints = mutableListOf()

        val navigationPointChildNodes = navigationPointNode.childNodes
        for (i in 0 until navigationPointChildNodes.length) {
            val navigationPointChildNode = navigationPointChildNodes.item(i)
            if (navigationPointChildNode is Element) {
                when (navigationPointChildNode.localName.toLowerCase()) {
                    "navlabel" -> {
                        val navigationLabel = readNavigationLabel(navigationPointChildNode)
                        result.navigationLabels!!.add(navigationLabel)
                    }

                    "content" -> {
                        val content = readNavigationContent(navigationPointChildNode)
                        result.content = content
                    }

                    "navpoint" -> {
                        val childNavigationPoint = readNavigationPoint(navigationPointChildNode)
                        result.childNavigationPoints!!.add(childNavigationPoint)
                    }
                }
            }
        }

        if (!result.navigationLabels!!.any()) {
            throw Exception("EPUB parsing error: navigation point ${result.id} should contain at least one navigation label.")
        }

        if (result.content == null) {
            throw Exception("EPUB parsing error: navigation point ${result.id} should contain content.")
        }

        return result
    }

    @JvmStatic
    private fun readNavigationLabel(navigationLabelNode: Element): EpubNavigationLabel {
        val result = EpubNavigationLabel()
        val navigationLabelTextNode = navigationLabelNode.getElementsByTagName("text").item(0) as Element?
                ?: throw Exception("Incorrect EPUB navigation label: label text element is missing.")
        result.text = navigationLabelTextNode.textContent
        return result
    }

    @JvmStatic
    private fun readNavigationContent(navigationContentNode: Element): EpubNavigationContent {
        val result = EpubNavigationContent()
        val navigationContentNodeAttributes = navigationContentNode.attributes
        for (i in 0 until navigationContentNodeAttributes.length) {
            val navigationContentNodeAttribute = navigationContentNodeAttributes.item(i)
            val attributeValue = navigationContentNodeAttribute.textContent
            when (navigationContentNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "src" -> result.source = attributeValue
            }
        }
        if (result.source.isNullOrBlank()) {
            throw Exception("Incorrect EPUB navigation content: content source is missing.")
        }
        return result
    }

    @JvmStatic
    private fun readNavigationPageList(navigationPageListNode: Element): EpubNavigationPageList {
        val result = EpubNavigationPageList()
        val pageTargetNodes = navigationPageListNode.getElementsByTagName("pageTarget")
        for (i in 0 until pageTargetNodes.length) {
            val pageTargetNode = pageTargetNodes.item(i) as Element
            val pageTarget = readNavigationPageTarget(pageTargetNode)
            result.add(pageTarget)
        }
        return result
    }

    @JvmStatic
    private fun readNavigationPageTarget(navigationPageTargetNode: Element): EpubNavigationPageTarget {
        val result = EpubNavigationPageTarget()
        val navigationPageTargetNodeAttributes = navigationPageTargetNode.attributes
        for (i in 0 until navigationPageTargetNodeAttributes.length) {
            val navigationPageTargetNodeAttribute = navigationPageTargetNodeAttributes.item(i)
            val attributeValue = navigationPageTargetNodeAttribute.textContent
            when (navigationPageTargetNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "value" -> result.value = attributeValue
                "class" -> result.className = attributeValue
                "playorder" -> result.playOrder = attributeValue
                "type" -> {
                    val type = try {
                        EpubNavigationPageTargetType.valueOf(attributeValue)
                    } catch (ex: IllegalArgumentException) {
                        throw Exception("Incorrect EPUB navigation page target: $attributeValue is incorrect value for page target type.")
                    }
                    result.type = type
                }
            }
        }

        if (result.type == null) {
            throw Exception("Incorrect EPUB navigation page target: page target type is missing.")
        }

        val navigationPageTargetChildNodes = navigationPageTargetNode.childNodes
        for (i in 0 until navigationPageTargetChildNodes.length) {
            val navigationPageTargetChildNode = navigationPageTargetChildNodes.item(i)
            if (navigationPageTargetChildNode is Element) {
                when (navigationPageTargetChildNode.localName.toLowerCase()) {
                    "navlabel" -> {
                        val navigationLabel = readNavigationLabel(navigationPageTargetChildNode)
                        result.navigationLabels!!.add(navigationLabel)
                    }

                    "content" -> {
                        val content = readNavigationContent(navigationPageTargetChildNode)
                        result.content = content
                    }
                }
            }
        }

        if (!result.navigationLabels!!.any()) {
            throw Exception("Incorrect EPUB navigation page target: at least one navLabel element is required.")
        }

        return result
    }

    @JvmStatic
    private fun readNavigationList(navigationListNode: Element): EpubNavigationList {
        val result = EpubNavigationList()
        val navigationListNodeAttributes = navigationListNode.attributes
        for (i in 0 until navigationListNodeAttributes.length) {
            val navigationListNodeAttribute = navigationListNodeAttributes.item(i)
            val attributeValue = navigationListNodeAttribute.textContent
            when (navigationListNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "class" -> result.className = attributeValue
            }
        }

        val navigationListChildNodes = navigationListNode.childNodes
        for (i in 0 until navigationListChildNodes.length) {
            val navigationListChildNode = navigationListChildNodes.item(i)
            if (navigationListChildNode is Element) {
                when (navigationListChildNode.localName.toLowerCase()) {
                    "navlabel" -> {
                        val navigationLabel = readNavigationLabel(navigationListChildNode)
                        result.navigationLabels!!.add(navigationLabel)
                    }

                    "navtarget" -> {
                        val navigationTarget = readNavigationTarget(navigationListChildNode)
                        result.navigationTargets!!.add(navigationTarget)
                    }
                }
            }
        }

        if (!result.navigationLabels!!.any()) {
            throw Exception("Incorrect EPUB navigation page target: at least one navLabel element is required.")
        }

        return result
    }

    @JvmStatic
    private fun readNavigationTarget(navigationTargetNode: Element): EpubNavigationTarget {
        val result = EpubNavigationTarget()
        val navigationPageTargetNodeAttributes = navigationTargetNode.attributes
        for (i in 0 until navigationPageTargetNodeAttributes.length) {
            val navigationPageTargetNodeAttribute = navigationPageTargetNodeAttributes.item(i)
            val attributeValue = navigationPageTargetNodeAttribute.textContent
            when (navigationPageTargetNodeAttribute.localName.toLowerCase()) {
                "id" -> result.id = attributeValue
                "value" -> result.value = attributeValue
                "class" -> result.className = attributeValue
                "playOrder" -> result.playOrder = attributeValue
            }
        }

        if (result.id.isNullOrBlank()) {
            throw Exception("Incorrect EPUB navigation target: navigation target ID is missing.")
        }

        val navigationTargetChildNodes = navigationTargetNode.childNodes
        for (i in 0 until navigationTargetChildNodes.length) {
            val navigationTargetChildNode = navigationTargetChildNodes.item(i)
            if (navigationTargetChildNode is Element) {
                when (navigationTargetChildNode.localName.toLowerCase()) {
                    "navlabel" -> {
                        val navigationLabel = readNavigationLabel(navigationTargetChildNode)
                        result.navigationLabels!!.add(navigationLabel)
                    }

                    "content" -> {
                        val content = readNavigationContent(navigationTargetChildNode)
                        result.content = content
                    }
                }
            }
        }

        if (!result.navigationLabels!!.any()) {
            throw Exception("Incorrect EPUB navigation target: at least one navLabel element is required.")
        }

        return result
    }
}