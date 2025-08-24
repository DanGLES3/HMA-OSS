package icu.nullptr.hidemyapplist.common.presets

import android.content.pm.ApplicationInfo
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
            if (manifestStr.contains("\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000m\u0000o\u0000d\u0000u\u0000l\u0000e")) {
                return true
            } else if (zipFile.getEntry("META-INF/xposed")?.isDirectory ?: false) {
                return true
            }
        }

        return false
    }
}
