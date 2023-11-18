package io.github.caimucheng.leaf.ide.application

import android.content.Intent
import android.os.Build
import android.os.Process
import io.github.caimucheng.leaf.ide.activity.CrashHandlerActivity
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.isAccessible

object AppCrashHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        val deviceInfo = buildList {
            val buildClass = Build::class
            val buildMembers = buildClass.staticProperties
            for (member in buildMembers) {
                val name = member.name
                val value = member.get()
                add("$name: " + buildString {
                    if (value is Array<*>) {
                        append(value.fullToString())
                    } else {
                        append(value)
                    }
                })
            }

            val buildVersionClass = Build.VERSION::class
            val buildVersionMembers = buildVersionClass.staticProperties
            for (member in buildVersionMembers) {
                member.isAccessible = true
                val name = member.name
                val value = member.get()
                add("$name: " + buildString {
                    if (value is Array<*>) {
                        append(value.fullToString())
                    } else {
                        append(value)
                    }
                })
            }
        }.joinToString(separator = "\n")

        val context = AppContext.current
        context.startActivity(Intent(context, CrashHandlerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("deviceInfo", deviceInfo)
            putExtra("threadGroup", t.threadGroup?.name ?: "null")
            putExtra("thread", t.name)
            putExtra("exception", e.stackTraceToString())
        })
        Process.killProcess(Process.myPid())
    }

    private fun Array<*>.fullToString(): String {
        return buildString {
            append("[")
            for ((index, element) in this@fullToString.withIndex()) {
                if (element is Array<*>) {
                    append(element.fullToString())
                } else {
                    append(element)
                }
                if (index + 1 < this@fullToString.size) {
                    append(", ")
                }
            }
            append("]")
        }
    }

}