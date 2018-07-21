package uk.co.hasali.readers

import uk.co.hasali.refentities.EpubBookRef

internal object BookCoverReader {
    @JvmStatic
    fun readBookCover(bookRef: EpubBookRef): ByteArray? {
        val metaItems = bookRef.schema?.epubPackage?.metadata?.metaItems
        if (metaItems == null || !metaItems.any()) {
            return null
        }

        val coverMetaItem = metaItems.firstOrNull { it.name!!.equals("cover", true) }
                ?: return null

        if (coverMetaItem.content.isNullOrEmpty()) {
            throw Exception("Incorrect EPUB metadata: cover item content is missing.")
        }

        val coverManifestItem = bookRef.schema?.epubPackage?.manifest?.firstOrNull { it.id.equals(coverMetaItem.content, true) }
                ?: throw Exception("Incorrect EPUB manifest: item with ID = \"${coverMetaItem.content}\" is missing.\"")

        val coverImageContentFileRef = bookRef.content?.images?.get(coverManifestItem.href)
                ?: throw Exception("Incorrect EPUB manifest: item with href = \"${coverManifestItem.href}\" is missing.")

        return coverImageContentFileRef.readContentAsBytes()
    }
}