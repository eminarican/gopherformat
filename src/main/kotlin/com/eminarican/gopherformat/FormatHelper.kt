package com.eminarican.gopherformat

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import java.awt.Color
import java.awt.Font
import java.util.regex.Pattern

object FormatHelper {

    const val TAG_START_SIZE = "<>".length
    const val TAG_END_SIZE = "</>".length

    private val holderPattern: Pattern = Pattern.compile("%v")
    private val tagPattern: Pattern = Pattern.compile("<([\\w-]+)>(.*?)</\\1>")

    val colorCode = mapOf(
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

    val fontCode = mapOf(
        "bold" to Font.BOLD,
        "italic" to Font.ITALIC,
    )

    fun iterateTags(
        text: String, offset: Int,
        callback: (rangeOut: TextRange, rangeIn: TextRange, key: String, offset: Int) -> Boolean
    ) {
        val matcher = tagPattern.matcher(text)

        while (matcher.find()) {
            val key = matcher.group(1)

            val rangeOut = TextRange(matcher.start(), matcher.end())
            val rangeIn = TextRange(
                rangeOut.startOffset + key.length + TAG_START_SIZE,
                rangeOut.endOffset - key.length - TAG_END_SIZE
            )

            val content = text.substring(rangeIn.startOffset, rangeIn.endOffset)
            if (callback(rangeOut, rangeIn, key, offset)) continue

            iterateTags(content, rangeIn.startOffset + offset, callback)
        }
    }

    fun iteratePlaceholders(
        text: String, offset: Int,
        callback: (range: TextRange) -> Unit
    ) {
        val matcher = holderPattern.matcher(text)

        while (matcher.find()) {
            callback(TextRange(matcher.start(), matcher.end()).shiftRight(offset))
        }
    }

    fun isStringLiteral(element: PsiElement): Boolean {
        return element.text.let {
            (it.startsWith("'") && it.endsWith("'")) ||
            (it.startsWith("\"") && it.endsWith("\""))
        }
    }

    fun findStringLiterals(element: PsiElement): Collection<PsiElement> {
        object : PsiElementProcessor.CollectElements<PsiElement>() {
            override fun execute(each: PsiElement): Boolean {
                return if (isStringLiteral(each)) false else super.execute(each)
            }
        }.let {
            PsiTreeUtil.processElements(element, it)
            return it.collection
        }
    }
}
