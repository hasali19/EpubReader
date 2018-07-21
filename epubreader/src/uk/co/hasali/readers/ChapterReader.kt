package uk.co.hasali.readers

import uk.co.hasali.refentities.EpubBookRef
import uk.co.hasali.refentities.EpubChapterRef
import uk.co.hasali.schema.navigation.EpubNavigationPoint

internal object ChapterReader {

    @JvmStatic
    fun getChapters(bookRef: EpubBookRef): MutableList<EpubChapterRef> =
            getChapters(bookRef, bookRef.schema?.navigation?.navMap)

    @JvmStatic
    fun getChapters(bookRef: EpubBookRef, navigationPoints: List<EpubNavigationPoint>?): MutableList<EpubChapterRef> {
        val result = mutableListOf<EpubChapterRef>()

        if (navigationPoints == null) {
            return result
        }

        for (navigationPoint in navigationPoints) {
            val contentFileName: String
            val anchor: String?

            val contentSourceAnchorCharIndex = navigationPoint.content!!.source!!.indexOf('#')

            if (contentSourceAnchorCharIndex == -1) {
                contentFileName = navigationPoint.content!!.source!!
                anchor = null
            } else {
                contentFileName = navigationPoint.content!!.source!!.substring(0, contentSourceAnchorCharIndex)
                anchor = navigationPoint.content!!.source!!.substring(contentSourceAnchorCharIndex + 1)
            }

            val htmlContentFileRef = bookRef.content!!.html!![contentFileName]
                    ?: throw Exception("Incorrect EPUB manifest: item with href = \"$contentFileName\" is missing.")

            val chapterRef = EpubChapterRef(htmlContentFileRef).apply {
                this.contentFileName = contentFileName
                this.anchor = anchor
                this.title = navigationPoint.navigationLabels!!.first().text
                this.subChapters = getChapters(bookRef, navigationPoint.childNavigationPoints)
            }

            result.add(chapterRef)
        }

        return result
    }
}