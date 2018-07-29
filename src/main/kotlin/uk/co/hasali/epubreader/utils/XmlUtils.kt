package uk.co.hasali.epubreader.utils

import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

internal object XmlUtils {
    fun loadDocument(stream: InputStream): Document {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val builder = factory.newDocumentBuilder()
        return builder.parse(stream)
    }
}
