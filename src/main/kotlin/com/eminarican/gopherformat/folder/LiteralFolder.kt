package com.eminarican.gopherformat.folder

import com.eminarican.gopherformat.FormatData
import com.eminarican.gopherformat.FormatHelper
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange

class LiteralFolder : FoldingBuilder {

    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()

        for (element in FormatHelper.findStringLiterals(node.psi)) {
            val foldGroup = FoldingGroup.newGroup("format_tags_fold")

            FormatHelper.iterateTags(element.text, element.textRange.startOffset) { rangeOut, _, key, offset ->
                descriptors.add(
                    FoldingDescriptor(
                        element.node, TextRange(
                            rangeOut.startOffset + offset,
                            rangeOut.startOffset + offset + key.length + FormatData.TAG_START_SIZE,
                        ), foldGroup, ""
                    )
                )
                descriptors.add(
                    FoldingDescriptor(
                        element.node, TextRange(
                            rangeOut.endOffset + offset - key.length - FormatData.TAG_END_SIZE,
                            rangeOut.endOffset + offset,
                        ), foldGroup, ""
                    )
                )
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
