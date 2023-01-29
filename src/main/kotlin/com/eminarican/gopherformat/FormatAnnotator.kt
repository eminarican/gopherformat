package com.eminarican.gopherformat

import com.goide.highlighting.GoSyntaxHighlightingColors
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Font

class FormatAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!FormatHelper.isStringLiteral(element)) return
        var mixed = false

        FormatHelper.iterateTags(element.text, element.textRange.startOffset) { rangeOut, _, key, offset ->
            !setColor(holder, key, rangeOut.shiftRight(offset))
            && !setFont(holder, key, rangeOut.shiftRight(offset), mixed).let {
                if (it) mixed = true
                it
            }
        }

        FormatHelper.iteratePlaceholders(element.text, element.textRange.startOffset) { range ->
            highlightPlaceholder(holder, range)
        }
    }

    private fun setColor(holder: AnnotationHolder, key: String, range: TextRange): Boolean {
        FormatHelper.colorCode[key]?.let {
            createAnnotation(holder, range, it)
            return true
        }
        return false
    }

    private fun setFont(holder: AnnotationHolder, key: String, range: TextRange, mixed: Boolean): Boolean {
        FormatHelper.fontCode[key]?.let {
            createAnnotation(holder, range, fontType = if(mixed) {
                Font.BOLD + Font.ITALIC
            } else {
                it
            })
            return true
        }
        return false
    }

    private fun highlightPlaceholder(holder: AnnotationHolder, range: TextRange) {
        createAnnotation(holder, range).textAttributes(GoSyntaxHighlightingColors.VALID_STRING_ESCAPE).create()
    }

    private fun createAnnotation(holder: AnnotationHolder, range: TextRange, textColor: Color? = null, fontType: Int = Font.PLAIN) {
        createAnnotation(holder, range).enforcedTextAttributes(
            TextAttributes(textColor, null, null, null, fontType)
        ).create()
    }

    private fun createAnnotation(holder: AnnotationHolder, range: TextRange): AnnotationBuilder {
        return holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(range)
    }
}
