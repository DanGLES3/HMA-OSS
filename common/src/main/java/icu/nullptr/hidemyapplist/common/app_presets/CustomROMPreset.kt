package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import icu.nullptr.hidemyapplist.common.Utils

class CustomROMPreset() : BasePreset("custom_rom") {
    override val exactPackageNames = setOf(
        "io.chaldeaprjkt.gamespace"
    )

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName

        // LineageOS overlays
        if (appInfo.sourceDir.contains("_lineage_")) {
            return true
        }

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

        // EvoX (just added by the community request)
        if (Utils.startsWithMultiple("org.evolution.", "org.evolutionx.") ||
            Utils.endsWithMultiple(".evolution", ".evolutionx")) {
            return true
        }

        // TODO: Add more custom ROM apps
        return false
    }
}
