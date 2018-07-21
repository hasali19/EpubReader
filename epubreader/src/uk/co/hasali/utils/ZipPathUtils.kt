package uk.co.hasali.utils

internal object ZipPathUtils {

    @JvmStatic
    fun getDirectoryPath(filePath: String): String {
        val lastSlashIndex = filePath.lastIndexOf('/')
        return if (lastSlashIndex == -1) {
            ""
        } else {
            filePath.substring(0, lastSlashIndex)
        }
    }

    @JvmStatic
    fun combine(directory: String?, fileName: String?): String? {
        return if (directory == null || directory.isEmpty()) {
            fileName
        } else {
            arrayOf(directory, fileName).joinToString("/")
        }
    }
}
