package icu.nullptr.hidemyapplist.common.presets

import android.content.pm.ApplicationInfo

abstract class BasePreset(val name: String) {
    val packageNames = mutableSetOf<String>()

    open fun addExactPackages() {}

    abstract fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean
}
