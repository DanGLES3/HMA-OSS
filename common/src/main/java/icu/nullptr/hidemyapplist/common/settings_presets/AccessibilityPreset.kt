package icu.nullptr.hidemyapplist.common.settings_presets

import android.provider.Settings

class AccessibilityPreset : BasePreset(NAME) {
    companion object {
        const val NAME = "accessibility"
    }

    override val settingsKVPairs = listOf(
        ReplacementItem(
            name = Settings.Secure.ACCESSIBILITY_ENABLED,
            value = "0",
        ),
        ReplacementItem(
            name = Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            value = "",
        ),
    )
}
