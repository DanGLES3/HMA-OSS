package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import icu.nullptr.hidemyapplist.common.Utils

class CustomROMPreset() : BasePreset("custom_rom") {
    override val exactPackageNames = setOf<String>()

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName

        // LineageOS apps
        if (Utils.startsWithMultiple(packageName, "lineageos.", "org.lineageos.")) {
            return true
        }

        // AOSPA
        if (packageName.startsWith("co.aospa.")) {
            return true
        }

        // OmniROM
        if (packageName.startsWith("org.omnirom.")) {
            return true
        }

        // ProtonAOSP
        if (packageName.startsWith("org.protonaosp.")) {
            return true
        }

        // TODO: Add more custom ROM apps
        return false
    }
}
