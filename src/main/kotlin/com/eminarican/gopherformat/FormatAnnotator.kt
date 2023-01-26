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

class FormatAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is GoStringLiteral) return
        var mixed = false

        FormatHelper.iterate(element.text, element.textRange.startOffset) { rangeOut, _, key, offset ->
            !setColor(holder, key, rangeOut.shiftRight(offset))
            && !setFont(holder, key, rangeOut.shiftRight(offset), mixed).let {
                if (it) mixed = it
                return@let it
            }
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
