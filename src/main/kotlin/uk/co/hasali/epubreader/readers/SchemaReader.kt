package uk.co.hasali.epubreader.readers

import uk.co.hasali.epubreader.entities.EpubSchema
import uk.co.hasali.epubreader.utils.ZipPathUtils
import uk.co.hasali.epubreader.zip.IZipFile

internal object SchemaReader {
    @JvmStatic
    fun readSchema(epubFile: IZipFile): EpubSchema {
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
