package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo

abstract class BasePreset(val name: String) {
    protected abstract val exactPackageNames: Set<String>
    protected val packageNames = mutableSetOf<String>()

    protected abstract fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean

    fun isDynamicListEmpty() = packageNames.isEmpty()

    fun containsPackage(packageName: String) = exactPackageNames.contains(packageName) || packageNames.contains(packageName)

    val packages get() = packageNames + exactPackageNames

    fun addPackageInfoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName
        if (!exactPackageNames.contains(packageName) && canBeAddedIntoPreset(appInfo)) {
            packageNames.add(packageName)
            return true
        }

        return false
    }

    fun removePackageFromPreset(packageName: String): Boolean {
        if (exactPackageNames.contains(packageName)) return false
        return packageNames.remove(packageName)
    }

    override fun toString() = "${javaClass.simpleName} {" +
            " \"exactPackageNames\": $exactPackageNames," +
            " \"packageNames\": $packageNames" +
            " }"
}
