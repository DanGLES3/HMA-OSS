package icu.nullptr.hidemyapplist.common.presets

import android.content.pm.ApplicationInfo

abstract class BasePreset(val name: String) {
    protected abstract val exactPackageNames: Set<String>
    protected val packageNames = mutableSetOf<String>()
    private var dynamicListFilled = false

    init {
        packageNames += exactPackageNames
    }

    protected abstract fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean

    fun isEmpty() = packageNames.isEmpty() || !dynamicListFilled

    fun containsPackage(packageName: String) = packageNames.contains(packageName)

    fun addPackageInfoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName
        if (!exactPackageNames.contains(packageName) && canBeAddedIntoPreset(appInfo)) {
            packageNames.add(packageName)
            dynamicListFilled = true
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
