package icu.nullptr.hidemyapplist.ui.util

import android.content.res.Resources
import org.frknkrc44.hma_oss.R

fun Boolean.enabledString(resources: Resources, lower: Boolean = false): String {
    val returnedStr = if (this) resources.getString(R.string.enabled)
    else resources.getString(R.string.disabled)

    return if (lower) returnedStr.lowercase() else returnedStr
}
