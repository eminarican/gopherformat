package com.eminarican.gopherformat.annotator

import com.eminarican.gopherformat.FormatHelper
import com.goide.psi.GoCallExpr
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class FunctionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is GoCallExpr) return
        if (!FormatHelper.isFormatFunction(element)) return

        FormatHelper.iterateFunctionSymbols(element) { ok, range, index, count ->
            if (!ok) addVerbWarning(holder, range, index, count)
            FormatHelper.highlightPlaceholder(holder, range)
        }
    }

    private fun addVerbWarning(holder: AnnotationHolder, range: TextRange, index: Int, count: Int) {
        FormatHelper.createWarning(holder, "No argument for verb: argument index = $index, arguments count = $count", range)
    }
}
