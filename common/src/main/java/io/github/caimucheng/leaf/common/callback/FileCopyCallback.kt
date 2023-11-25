package io.github.caimucheng.leaf.common.callback

interface FileCopyCallback {

    fun onCopySuccess()

    fun onCopyFailed(e: Exception)

}