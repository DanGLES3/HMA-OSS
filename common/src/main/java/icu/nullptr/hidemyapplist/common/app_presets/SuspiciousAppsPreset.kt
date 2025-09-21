package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import java.util.zip.ZipFile

class SuspiciousAppsPreset : BasePreset("sus_apps") {
    override val exactPackageNames = setOf(
        "com.reveny.vbmetafix.service",
        "com.speedsoftware.rootexplorer",
        "me.zhanghai.android.files",
        "com.lonelycatgames.Xplore",
    )

    val libNames = arrayOf(
        "liblsplant.so",
    )

    val assetNames = arrayOf(
        // ~All possible APK editors
        "APKEditor.pk8",
        "testkey.pk8",
        "key/testkey.pk8",

        // TODO: Add more suspicious apps by checking for files
    )

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName

        // Termux, all of its plugins and some of Termux forks
        if (packageName.startsWith("com.termux")) {
            return true
        }

        // All RealVNC apps (categorized as suspicious, because some of apps checking for them)
        if (packageName.startsWith("com.realvnc.")) {
            return true
        }

        // FX File Manager
        if (packageName.startsWith("nextapp.fx")) {
            return true
        }

        // TotalCommander and its plugins
        if (packageName.startsWith("com.ghisler.")) {
            return true
        }

        // ZDevs apps (ZArchiver etc.)
        if (packageName.startsWith("ru.zdevs.")) {
            return true
        }

        // MiXplorer, MiXplorer Silver and its plugins
        if (packageName.startsWith("com.mixplorer")) {
            return true
        }

        // MT Manager
        if (packageName.startsWith("bin.mt.plus")) {
            return true
        }

        ZipFile(appInfo.sourceDir).use { zipFile ->
            if (findAppsFromLibs(zipFile, libNames) || findAppsFromAssets(zipFile, assetNames)) {
                return true
            }
        }

        // TODO: Add more suspicious apps
        return false
    }
}