package com.eminarican.gopherformat

import com.goide.psi.GoStringLiteral
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil

class FormatFoldingBuilder : FoldingBuilder {

    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()

        for (element in PsiTreeUtil.findChildrenOfType(node.psi, GoStringLiteral::class.java)) {
            FormatHelper.iterate(element.text, element.textRange.startOffset) { rangeOut, _, key, offset ->
                descriptors.add(FoldingDescriptor(element, TextRange(
                    rangeOut.startOffset + offset, rangeOut.startOffset + key.length + 2 + offset
                )))
                descriptors.add(FoldingDescriptor(element, TextRange(
                    rangeOut.endOffset - key.length - 3 + offset, rangeOut.endOffset + offset
                )))
                false
            }
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return ""
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return true
    }
}
