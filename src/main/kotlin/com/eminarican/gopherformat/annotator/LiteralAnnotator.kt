package com.eminarican.gopherformat.annotator

import com.eminarican.gopherformat.FormatData
import com.eminarican.gopherformat.FormatHelper
import com.goide.highlighting.GoSyntaxHighlightingColors
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.awt.Font

class LiteralAnnotator : Annotator {

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
        FormatData.ColorCode[key]?.let {
            FormatHelper.createAnnotation(holder, range, it)
            return true
        }
        return false
    }

    private fun setFont(holder: AnnotationHolder, key: String, range: TextRange, mixed: Boolean): Boolean {
        FormatData.FontCode[key]?.let {
            FormatHelper.createAnnotation(holder, range, fontType = if(mixed) {
                Font.BOLD + Font.ITALIC
            } else {
                it
            })
            return true
        }
        return false
    }

    private fun highlightPlaceholder(holder: AnnotationHolder, range: TextRange) {
        FormatHelper.createAnnotation(holder, range).textAttributes(GoSyntaxHighlightingColors.VALID_STRING_ESCAPE).create()
    }
}
