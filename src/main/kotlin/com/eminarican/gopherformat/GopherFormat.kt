package com.eminarican.gopherformat

import com.goide.psi.GoStringLiteral
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Font
import java.util.regex.Pattern

class GopherFormat : Annotator {

    private val pattern = Pattern.compile("<(\\w+)>(.*?)</\\1>")

    private val colorCode = mapOf(
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
    private val fontCode = mapOf(
        "bold" to Font.BOLD,
        "italic" to Font.ITALIC,
    )

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is GoStringLiteral) return
        highlight(holder, element.text, element.textRange.startOffset, false)
    }

    private fun highlight(holder: AnnotationHolder, text: String, offset: Int, mixed: Boolean) {
        val matcher = pattern.matcher(text)
        var mixed = mixed

        while (matcher.find()) {
            val key = matcher.group(1)

            val rangeOut = TextRange(matcher.start(), matcher.end())
            val rangeIn = TextRange(
                rangeOut.startOffset + key.length + 2,
                rangeOut.endOffset - key.length - 3
            )

            val content = text.substring(rangeIn.startOffset, rangeIn.endOffset)

            if (
                !setColor(holder, key, rangeOut.shiftRight(offset))
                && !setFont(holder, key, rangeOut.shiftRight(offset), mixed).let {
                    if (it) mixed = it
                    return@let it
                }
            ) continue
            highlight(holder, content, rangeIn.startOffset + offset, mixed)
        }
    }

    private fun setColor(holder: AnnotationHolder, key: String, range: TextRange): Boolean {
        colorCode[key]?.let {
            createAnnotation(holder, range, it)
            return true
        }
        return false
    }

    private fun setFont(holder: AnnotationHolder, key: String, range: TextRange, mixed: Boolean): Boolean {
        fontCode[key]?.let {
            createAnnotation(holder, range, fontType = if(!mixed) {
                it
            } else {
                Font.BOLD + Font.ITALIC
            })
            return true
        }
        return false
    }

    private fun createAnnotation(holder: AnnotationHolder, range: TextRange, textColor: Color? = null, fontType: Int = Font.PLAIN) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(range)
            .enforcedTextAttributes(TextAttributes(textColor, null, null, null, fontType)).create()
    }
}
