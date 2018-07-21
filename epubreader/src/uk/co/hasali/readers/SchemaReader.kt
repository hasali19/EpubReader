package uk.co.hasali.readers

import uk.co.hasali.entities.EpubSchema
import uk.co.hasali.utils.ZipPathUtils
import java.util.zip.ZipFile

internal object SchemaReader {
    @JvmStatic
    fun readSchema(epubFile: ZipFile): EpubSchema {
        val result = EpubSchema()
        val rootFilePath = RootFilePathReader.getRootFilePath(epubFile)
        val contentDirectoryPath = ZipPathUtils.getDirectoryPath(rootFilePath)
        result.contentDirectoryPath = contentDirectoryPath
        val epubPackage = PackageReader.readPackage(epubFile, rootFilePath)
        result.epubPackage = epubPackage
        val navigation = NavigationReader.readNavigation(epubFile, contentDirectoryPath, epubPackage)
        result.navigation = navigation
        return result
    }
}
