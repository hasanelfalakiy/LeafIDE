package io.github.caimucheng.leaf.ide.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.caimucheng.leaf.common.callback.FileCopyCallback
import io.github.caimucheng.leaf.common.fragment.FileCopyFragment
import io.github.caimucheng.leaf.ide.activity.MainActivity
import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PluginBroadcastReceiver : BroadcastReceiver() {

    private val coroutineScope =
        CoroutineScope(Dispatchers.Main + CoroutineName("PluginBroadcastCoroutine"))

    private val mutex = Mutex()

    override fun onReceive(context: Context, intent: Intent) {
        val extraReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
        val packageName = intent.dataString?.removePrefix("package:") ?: return
        when {
            !extraReplacing && intent.action == Intent.ACTION_PACKAGE_ADDED -> {
                Log.e("PluginBroadcast", "Package Added")
                debugInstallPlugin(context, packageName)
            }

            !extraReplacing && intent.action == Intent.ACTION_PACKAGE_REMOVED -> {
                Log.e("PluginBroadcast", "Package Removed")
                debugUninstallPlugin(packageName)
            }

            intent.action == Intent.ACTION_PACKAGE_REPLACED -> {
                Log.e("PluginBroadcastReceiver", "Package Replaced")
                debugUpdatePlugin(context, packageName)
            }
        }
    }

    private fun debugInstallPlugin(context: Context, packageName: String) {
        coroutineScope.launch {
            mutex.lock()
            try {
                val packageManager = context.packageManager
                val applicationInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val metaData = applicationInfo.metaData ?: return@launch
                if (metaData.getBoolean("leafide_plugin", false)) {
                    val activity = MainActivity.currentMainActivity?.get() ?: return@launch
                    val fragmentManager = activity.supportFragmentManager
                    copyFile(packageName, applicationInfo.sourceDir, fragmentManager, activity)
                    AppViewModel.intent.send(
                        AppIntent.Install(
                            packageName,
                            activity,
                            fragmentManager
                        )
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
            } finally {
                mutex.unlock()
            }
        }
    }

    private fun debugUninstallPlugin(packageName: String) {
        coroutineScope.launch {
            mutex.lock()
            try {
                if (AppViewModel.state.value.plugins.find { it.packageName == packageName } != null) {
                    val activity = MainActivity.currentMainActivity?.get() ?: return@launch
                    val fragmentManager = activity.supportFragmentManager
                    deleteFile(packageName)
                    AppViewModel.intent.send(
                        AppIntent.Uninstall(
                            packageName,
                            activity,
                            fragmentManager
                        )
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
            } finally {
                mutex.unlock()
            }
        }
    }

    private fun debugUpdatePlugin(context: Context, packageName: String) {
        coroutineScope.launch {
            mutex.lock()
            try {
                val packageManager = context.packageManager
                val applicationInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val metaData = applicationInfo.metaData ?: return@launch
                if (metaData.getBoolean("leafide_plugin", false)) {
                    val activity = MainActivity.currentMainActivity?.get() ?: return@launch
                    val fragmentManager = activity.supportFragmentManager
                    copyFile(packageName, applicationInfo.sourceDir, fragmentManager, activity)
                    AppViewModel.intent.send(
                        AppIntent.Update(
                            packageName,
                            activity,
                            fragmentManager
                        )
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
            } finally {
                mutex.unlock()
            }
        }
    }

    private suspend fun deleteFile(
        packageName: String
    ) {
        suspendCoroutine { continuation ->
            if (File(LeafIDEPluginRootPath, "${packageName}.apk").delete()) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(IOException("Delete plugin failed"))
            }
        }
    }

    private suspend fun copyFile(
        packageName: String,
        sourceDir: String,
        fragmentManager: FragmentManager,
        activity: MainActivity
    ) {
        suspendCoroutine { continuation ->
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    activity.lifecycle.removeObserver(this)
                    super.onStart(owner)
                    val fileCopyFragment = FileCopyFragment()
                    fileCopyFragment.arguments = bundleOf(
                        "name" to packageName,
                        "from" to sourceDir,
                        "to" to File(LeafIDEPluginRootPath, "${packageName}.apk").absolutePath
                    )
                    fileCopyFragment.setFileCopyCallback(object : FileCopyCallback {

                        override fun onCopySuccess() {
                            fileCopyFragment.dismiss()
                            continuation.resume(Unit)
                        }

                        override fun onCopyFailed(e: Exception) {
                            fileCopyFragment.dismiss()
                            continuation.resumeWithException(e)
                        }

                    })
                    fileCopyFragment.isCancelable = false
                    fileCopyFragment.show(fragmentManager, null)
                }
            })
        }
    }

}