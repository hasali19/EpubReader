package uk.co.hasali.readers

import org.w3c.dom.Element
import uk.co.hasali.utils.XmlUtils
import uk.co.hasali.zip.IZipFile
import java.util.zip.ZipFile

private const val EPUB_CONTAINER_FILE_PATH = "META-INF/container.xml"

internal object RootFilePathReader {
    @JvmStatic
    fun getRootFilePath(epubFile: IZipFile): String {
        val containerFileEntry = epubFile.getEntry(EPUB_CONTAINER_FILE_PATH)
                ?: throw Exception("EPUB parsing error: $EPUB_CONTAINER_FILE_PATH file not found in archive.")
        val containerDocument = containerFileEntry.getInputStream().use { stream ->
            XmlUtils.loadDocument(stream)
        }

        val rootNode = containerDocument.getElementsByTagName("container").item(0) as Element?
        val rootFilesNode = rootNode?.getElementsByTagName("rootfiles")?.item(0) as Element?
        val rootFileNode = rootFilesNode?.getElementsByTagName("rootfile")?.item(0) as Element?

        return rootFileNode?.getAttribute("full-path")
                ?: throw Exception("EPUB parsing error: root file path not found in the EPUB container.")
    }
}