package uk.co.hasali.zip

import java.io.InputStream

interface IZipEntry {
    val size: Long
    fun getInputStream(): InputStream
}
