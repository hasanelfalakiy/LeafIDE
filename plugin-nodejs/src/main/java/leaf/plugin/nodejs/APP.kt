package leaf.plugin.nodejs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.callback.FileDeleteCallback
import io.github.caimucheng.leaf.common.callback.FileUnZipCallback
import io.github.caimucheng.leaf.common.fragment.FileDeleteFragment
import io.github.caimucheng.leaf.common.fragment.FileUnZipFragment
import io.github.caimucheng.leaf.plugin.PluginAPP
import io.github.caimucheng.leaf.plugin.creator.FragmentCreator
import io.github.caimucheng.leaf.plugin.path.Paths
import leaf.plugin.nodejs.creator.APPFragmentCreator
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("unused")
class APP : PluginAPP() {

    companion object {

        const val PACKAGE_NAME = "leaf.plugin.nodejs.APP"

        @SuppressLint("StaticFieldLeak")
        lateinit var currentContext: Context
            private set

        lateinit var currentResources: Resources
            private set

        lateinit var currentPaths: Paths
            private set

    }

    override fun onCreate(hostApplicationContext: Context, resources: Resources, paths: Paths) {
        currentContext = hostApplicationContext
        currentResources = resources
        currentPaths = paths;
    }

    override suspend fun onInstall(activityContext: Context, fragmentManager: FragmentManager) {
        super.onInstall(activityContext, fragmentManager)
        suspendCoroutine { continuation ->
            MaterialAlertDialogBuilder(activityContext)
                .setTitle(currentResources.getString(R.string.install_nodejs))
                .setMessage(currentResources.getString(R.string.install_nodejs_message))
                .setCancelable(false)
                .setPositiveButton(currentResources.getString(R.string.install)) { _, _ ->
                    installNodeJS(
                        activityContext,
                        fragmentManager,
                        continuation,
                        isUpdatedMode = false
                    )
                }
                .show()
        }
    }

    private fun installNodeJS(
        activityContext: Context,
        fragmentManager: FragmentManager,
        continuation: Continuation<Unit>,
        isUpdatedMode: Boolean
    ) {
        val fileUnZipFragment = FileUnZipFragment()
        fileUnZipFragment.setAssets(currentResources.assets)
        fileUnZipFragment.arguments = bundleOf(
            "name" to "nodejs.tgz",
            "from" to "nodejs.tgz",
            "to" to File(activityContext.filesDir, "isolate").absolutePath,
            "type" to "gz"
        )
        fileUnZipFragment.setAssets(currentResources.assets)
        fileUnZipFragment.setFileUnZipCallback(object : FileUnZipCallback {

            override fun onUnZipSuccess() {
                fileUnZipFragment.dismiss()
                if (isUpdatedMode) {
                    Toasty.success(
                        activityContext,
                        currentResources.getString(R.string.update_nodejs_successfully),
                        Toasty.LENGTH_SHORT
                    ).show()
                } else {
                    Toasty.success(
                        activityContext,
                        currentResources.getString(R.string.install_nodejs_successfully),
                        Toasty.LENGTH_SHORT
                    ).show()
                }
                continuation.resume(Unit)
            }

            override fun onUnZipFailed(e: Exception) {
                fileUnZipFragment.dismiss()
                if (isUpdatedMode) {
                    Toasty.error(
                        activityContext,
                        currentResources.getString(R.string.update_nodejs_failed, e.message),
                        Toasty.LENGTH_LONG
                    ).show()
                } else {
                    Toasty.error(
                        activityContext,
                        currentResources.getString(R.string.install_nodejs_failed, e.message),
                        Toasty.LENGTH_LONG
                    ).show()
                }
                continuation.resume(Unit)
            }

        })
        fileUnZipFragment.isCancelable = false
        fileUnZipFragment.show(fragmentManager, "UnZipNodeJSTask")
    }

    override suspend fun onUninstall(activityContext: Context, fragmentManager: FragmentManager) {
        super.onUninstall(activityContext, fragmentManager)
        suspendCoroutine { continuation ->
            MaterialAlertDialogBuilder(activityContext)
                .setTitle(currentResources.getString(R.string.uninstall_nodejs))
                .setMessage(currentResources.getString(R.string.uninstall_nodejs_message))
                .setCancelable(false)
                .setPositiveButton(currentResources.getString(R.string.uninstall)) { _, _ ->
                    uninstallNodeJS(activityContext, fragmentManager, continuation)
                }
                .show()
        }
    }

    private fun uninstallNodeJS(
        activityContext: Context,
        fragmentManager: FragmentManager,
        continuation: Continuation<Unit>
    ) {
        val isolateFolder = File(activityContext.filesDir, "isolate")
        val fileDeleteFragment = FileDeleteFragment()
        fileDeleteFragment.arguments = bundleOf(
            "name" to "nodejs",
            "path" to isolateFolder.absolutePath
        )
        fileDeleteFragment.setFileDeleteCallback(object : FileDeleteCallback {

            override fun onDeleteSuccess() {
                fileDeleteFragment.dismiss()
                Toasty.success(
                    activityContext,
                    currentResources.getString(R.string.uninstall_nodejs_successfully),
                    Toasty.LENGTH_SHORT
                ).show()
                continuation.resume(Unit)
            }

            override fun onDeleteFailed() {
                fileDeleteFragment.dismiss()
                Toasty.error(
                    activityContext,
                    currentResources.getString(R.string.uninstall_nodejs_failed),
                    Toasty.LENGTH_LONG
                ).show()
                continuation.resume(Unit)
            }

        })
        fileDeleteFragment.isCancelable = false
        fileDeleteFragment.show(fragmentManager, "UninstallNodeJSTask")
    }

    override suspend fun onUpdate(activityContext: Context, fragmentManager: FragmentManager) {
        super.onUpdate(activityContext, fragmentManager)
        suspendCoroutine { continuation ->
            MaterialAlertDialogBuilder(activityContext)
                .setTitle(currentResources.getString(R.string.update_nodejs))
                .setMessage(currentResources.getString(R.string.update_nodejs_message))
                .setCancelable(false)
                .setPositiveButton(currentResources.getString(R.string.update)) { _, _ ->
                    updateNodeJS(activityContext, fragmentManager, continuation)
                }
                .show()
        }
    }

    private fun updateNodeJS(
        activityContext: Context,
        fragmentManager: FragmentManager,
        continuation: Continuation<Unit>
    ) {
        val isolateFolder = File(activityContext.filesDir, "isolate")
        val fileDeleteFragment = FileDeleteFragment()
        fileDeleteFragment.arguments = bundleOf(
            "name" to "nodejs",
            "path" to isolateFolder.absolutePath
        )
        fileDeleteFragment.setFileDeleteCallback(object : FileDeleteCallback {

            override fun onDeleteSuccess() {
                fileDeleteFragment.dismiss()
                installNodeJS(
                    activityContext,
                    fragmentManager,
                    continuation,
                    isUpdatedMode = true
                )
            }

            override fun onDeleteFailed() {
                fileDeleteFragment.dismiss()
                Toasty.error(
                    activityContext,
                    currentResources.getString(R.string.delete_failed),
                    Toasty.LENGTH_LONG
                ).show()
                continuation.resume(Unit)
            }

        })
        fileDeleteFragment.isCancelable = false
        fileDeleteFragment.show(fragmentManager, "UpdateNodeJSTask")
    }

    override fun getFragmentCreator(): FragmentCreator {
        return APPFragmentCreator
    }

    override fun getPluginName(): String {
        return currentResources.getString(R.string.app_name)
    }

    override fun getPluginDescription(): String {
        return currentResources.getString(R.string.plugin_description)
    }

    override fun getPluginAuthor(): String {
        return currentResources.getString(R.string.plugin_author)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getProjectCardIcon(): Drawable {
        return currentResources.getDrawable(R.drawable.nodejs_logo, currentContext.theme)
    }

    override fun getProjectCardSubscript(): String {
        return currentResources.getString(R.string.project_card_subscript)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getTemplateIcon(): Drawable {
        return currentResources.getDrawable(R.mipmap.template_icon, currentContext.theme)
    }

    override fun getTemplateTitle(): String {
        return currentResources.getString(R.string.template_title)
    }

}