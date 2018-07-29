package uk.co.hasali.epubreader.schema.navigation

class EpubNavigation {
    var head: EpubNavigationHead? = null
    var docTitle: EpubNavigationDocTitle? = null
    var docAuthors: MutableList<EpubNavigationDocAuthor>? = null
    var navMap: EpubNavigationMap? = null
    var pageList: EpubNavigationPageList? = null
    var navLists: MutableList<EpubNavigationList>? = null
}
