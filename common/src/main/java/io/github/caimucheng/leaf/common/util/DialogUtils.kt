package io.github.caimucheng.leaf.common.util

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showDialog(
    titleResId: Int,
    messageResId: Int,
    cancelable: Boolean = true,
    positiveTextResId: Int? = null,
    positiveEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> },
    negativeTextResId: Int? = null,
    negativeEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> },
    neutralTextResId: Int? = null,
    neutralEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> }
) {
    showDialog(
        title = getString(titleResId),
        message = getString(messageResId),
        cancelable = cancelable,
        positiveText = if (positiveTextResId == null) null else getString(positiveTextResId),
        positiveEvent = positiveEvent,
        negativeText = if (negativeTextResId == null) null else getString(negativeTextResId),
        negativeEvent = negativeEvent,
        neutralText = if (neutralTextResId == null) null else getString(neutralTextResId),
        neutralEvent = neutralEvent
    )
}

fun Context.showDialog(
    title: String,
    message: String,
    cancelable: Boolean = true,
    positiveText: String? = null,
    positiveEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> },
    negativeText: String? = null,
    negativeEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> },
    neutralText: String? = null,
    neutralEvent: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> }
) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setTitle(title)
        .setMessage(message)
    positiveText?.let {
        dialog.setPositiveButton(positiveText) { dialog, which -> positiveEvent(dialog, which) }
    }
    negativeText?.let {
        dialog.setNegativeButton(negativeText) { dialog, which -> negativeEvent(dialog, which) }
    }
    neutralText?.let {
        dialog.setNeutralButton(neutralText) { dialog, which -> neutralEvent(dialog, which) }
    }
    dialog.show()
}