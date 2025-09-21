package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import icu.nullptr.hidemyapplist.common.CommonUtils
import java.util.zip.ZipFile

class XposedModulesPreset() : BasePreset("xposed") {
    override val exactPackageNames = setOf<String>()

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        ZipFile(appInfo.sourceDir).use { zipFile ->
            val manifestFile = zipFile.getInputStream(
                zipFile.getEntry("AndroidManifest.xml"))
            val manifestBytes = manifestFile.use { it.readBytes() }
            val manifestStr = String(manifestBytes, Charsets.US_ASCII)

            // Checking with binary because the Android system sucks
            if (manifestStr.contains(CommonUtils.XPOSED_PROP1) ||
                manifestStr.contains(CommonUtils.XPOSED_PROP2)) {
                return true
            } else if (zipFile.getEntry("META-INF/xposed/module.prop") != null) {
                return true
            }
        }

        return false
    }
}
