package uk.co.hasali.epubreader.zip

import java.io.InputStream

interface IZipEntry {
    val size: Long
    fun getInputStream(): InputStream
}
