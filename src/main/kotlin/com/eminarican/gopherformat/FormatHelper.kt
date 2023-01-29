package com.eminarican.gopherformat

import com.goide.highlighting.GoSyntaxHighlightingColors
import com.goide.psi.GoCallExpr
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import java.awt.Color
import java.awt.Font

object FormatHelper {

    fun iterateTags(
        text: String, offset: Int,
        callback: (rangeOut: TextRange, rangeIn: TextRange, key: String, offset: Int) -> Boolean
    ) {
        FormatData.TagPattern.matcher(text).results().forEach {
            val key = it.group(1)

            val rangeOut = TextRange(it.start(), it.end())
            val rangeIn = TextRange(
                rangeOut.startOffset + key.length + FormatData.TAG_START_SIZE,
                rangeOut.endOffset - key.length - FormatData.TAG_END_SIZE
            )

            val content = text.substring(rangeIn.startOffset, rangeIn.endOffset)
            if (!callback(rangeOut, rangeIn, key, offset)) {
                iterateTags(content, rangeIn.startOffset + offset, callback)
            }
        }
    }

    fun iterateSymbols(
        text: String, offset: Int,
        callback: (range: TextRange) -> Unit
    ) {
        FormatData.HolderPattern.matcher(text).results().forEach {
            callback(TextRange(it.start(), it.end()).shiftRight(offset))
        }
    }

    fun iterateFunctionSymbols(
        expression: GoCallExpr,
        callback: (ok: Boolean, range: TextRange, index: Int, count: Int) -> Unit
    ) {
        val expressions = expression.argumentList.expressionList
        val first = expressions.first() ?: return

        first.value?.string?.let { value ->
            val verbCount = value.split(FormatData.HolderPattern).size - 1
            val argCount = expressions.size - 1

            if (verbCount < 1) return

            var count = 1
            iterateSymbols(value, first.textRange.startOffset.inc()) { range ->
                callback(count <= argCount, range, count, argCount)
                count++
            }
        }
    }

    fun isStringLiteral(element: PsiElement): Boolean {
        return element.text.let {
            (it.startsWith("'") && it.endsWith("'")) ||
            (it.startsWith("\"") && it.endsWith("\""))
        }
    }

    fun isFormatFunction(element: GoCallExpr): Boolean {
        return element.expression.text.contains(FormatData.FORMAT_FUNCTION)
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

    fun createAnnotation(holder: AnnotationHolder, range: TextRange, textColor: Color? = null, fontType: Int = Font.PLAIN) {
        createAnnotation(holder, range).enforcedTextAttributes(
            TextAttributes(textColor, null, null, null, fontType)
        ).create()
    }

    fun createAnnotation(holder: AnnotationHolder, range: TextRange): AnnotationBuilder {
        return holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(range)
    }

    fun createWarning(holder: AnnotationHolder, message: String, range: TextRange) {
        return holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message).range(range).create()
    }

    fun highlightPlaceholder(holder: AnnotationHolder, range: TextRange) {
        createAnnotation(holder, range).textAttributes(GoSyntaxHighlightingColors.VALID_STRING_ESCAPE).create()
    }
}
