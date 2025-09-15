package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import java.util.zip.ZipFile

class XposedModulesPreset() : BasePreset("xposed") {
    override val exactPackageNames = setOf<String>()

    companion object {
        private const val XPOSED_PROP1 = "\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000m\u0000o\u0000d\u0000u\u0000l\u0000e"
        private const val XPOSED_PROP2 = "\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000s\u0000c\u0000o\u0000p\u0000e"
    }

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        ZipFile(appInfo.sourceDir).use { zipFile ->
            val manifestFile = zipFile.getInputStream(
                zipFile.getEntry("AndroidManifest.xml"))
            val manifestBytes = manifestFile.use { it.readBytes() }
            val manifestStr = String(manifestBytes, Charsets.US_ASCII)

            // Checking with binary because the Android system sucks
            if (manifestStr.contains(XPOSED_PROP1) || manifestStr.contains(XPOSED_PROP2)) {
                return true
            } else if (zipFile.getEntry("META-INF/xposed/module.prop") != null) {
                return true
            }
        }

        return false
    }
}
