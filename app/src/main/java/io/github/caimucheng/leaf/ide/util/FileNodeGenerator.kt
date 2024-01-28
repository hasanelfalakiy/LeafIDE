package io.github.caimucheng.leaf.ide.util

import io.github.caimucheng.leaf.ide.treeview.FileListLoader
import io.github.dingyi222666.view.treeview.AbstractTree
import io.github.dingyi222666.view.treeview.Tree
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeGenerator
import java.io.File

class FileNodeGenerator(
    private val rootPath: File,
    private val fileListLoader: FileListLoader
) : TreeNodeGenerator<File> {
    override suspend fun fetchChildData(targetNode: TreeNode<File>): Set<File> {
        val path = targetNode.requireData().absolutePath
        var files = fileListLoader.getCacheFileList(path)
        if (files.isEmpty()) files = fileListLoader.loadFileList(path)
        return files.toSet()
    }

    override fun createNode(
        parentNode: TreeNode<File>,
        currentData: File,
        tree: AbstractTree<File>
    ): TreeNode<File> {
        return TreeNode(
            data = currentData,
            depth = parentNode.depth + 1,
            name = currentData.name,
            id = tree.generateId(),
            hasChild = currentData.isDirectory && fileListLoader.getCacheFileList(currentData.absolutePath)
                .isNotEmpty(),
            isChild = currentData.isDirectory,
            expand = false
        )

    }

    override fun createRootNode(): TreeNode<File> {
        return TreeNode(
            data = rootPath,
            depth = -1,
            name = rootPath.name,
            id = Tree.ROOT_NODE_ID,
            hasChild = true,
            isChild = true,
        )
    }
}