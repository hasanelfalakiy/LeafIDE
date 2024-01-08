package io.github.caimucheng.leaf.ide.model

class TreeNode<T>(
    val value: T,
    val level: Int = 1
) {
    val children: MutableList<TreeNode<T>> = mutableListOf()
    var expand = false
        set(value) {
            field = value
            if (!value) {
                children.forEach { it.expand = false }
            }
        }

    fun addChild(vararg node: TreeNode<T>) {
        node.forEach { children.add(it) }
    }
}
