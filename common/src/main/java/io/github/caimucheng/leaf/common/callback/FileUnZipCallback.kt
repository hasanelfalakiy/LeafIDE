package io.github.caimucheng.leaf.common.callback

interface FileUnZipCallback {

    fun onUnZipSuccess()

    fun onUnZipFailed(e: Exception)

}