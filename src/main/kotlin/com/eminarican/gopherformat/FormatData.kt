package com.eminarican.gopherformat

import java.awt.Color
import java.awt.Font
import java.util.regex.Pattern

object FormatData {

    const val TAG_START_SIZE = "<>".length
    const val TAG_END_SIZE = "</>".length

    const val FORMAT_FUNCTION = "Colourf"

    val HolderPattern: Pattern = Pattern.compile("%[bcdeEfFgGopqstTUvVxX]")
    val TagPattern: Pattern = Pattern.compile("<([\\w-]+)>(.*?)</\\1>")

    val ColorCode = mapOf(
        "black" to Color(0, 0, 0),

        "red" to Color(255, 85, 85),
        "gold" to Color(255, 170, 0),
        "blue" to Color(85, 85, 255),
        "green" to Color(85, 255, 85),
        "aqua" to Color(85, 255, 255),
        "grey" to Color(170, 170, 170),
        "purple" to Color(255, 85, 255),
        "yellow" to Color(255, 255, 85),
        "white" to Color(255, 255, 255),

        "dark-red" to Color(170, 0, 0),
        "dark-blue" to Color(0, 0, 170),
        "dark-green" to Color(0, 170, 0),
        "dark-grey" to Color(85, 85, 85),
        "dark-aqua" to Color(0, 170, 170),
        "dark-purple" to Color(170, 0, 170),
        "dark-yellow" to Color(221, 214, 5),
    )

    val FontCode = mapOf(
        "bold" to Font.BOLD,
        "italic" to Font.ITALIC,
    )
}
