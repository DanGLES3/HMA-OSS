package icu.nullptr.hidemyapplist.common.settings_presets

import android.provider.Settings

class DeveloperOptionsPreset : BasePreset("dev_options") {
    override val settingsKVPairs = listOf(
        ReplacementItem(
            name = Settings.Global.ADB_ENABLED,
            value = "0",
        ),
        ReplacementItem(
            name = "adb_wifi_enabled",
            value = "0",
        ),
        ReplacementItem(
            name = Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            value = "0",
        ),
    )
}
