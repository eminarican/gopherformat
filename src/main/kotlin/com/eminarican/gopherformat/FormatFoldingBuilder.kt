package com.eminarican.gopherformat

import com.goide.psi.GoStringLiteral
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil

class FormatFoldingBuilder : FoldingBuilder {

    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()

        for (element in PsiTreeUtil.findChildrenOfType(node.psi, GoStringLiteral::class.java)) {
            val foldGroup = FoldingGroup.newGroup("format_tags_fold")

            FormatHelper.iterate(element.text, element.textRange.startOffset) { rangeOut, _, key, offset ->
                descriptors.add(FoldingDescriptor(element.node, TextRange(
                    rangeOut.startOffset + offset, rangeOut.startOffset + key.length + 2 + offset
                ), foldGroup, ""))
                descriptors.add(FoldingDescriptor(element.node, TextRange(
                    rangeOut.endOffset - key.length - 3 + offset, rangeOut.endOffset + offset
                ), foldGroup, ""))
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
