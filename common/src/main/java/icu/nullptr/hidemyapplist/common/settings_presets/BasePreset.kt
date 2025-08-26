package icu.nullptr.hidemyapplist.common.settings_presets

abstract class BasePreset(val name: String) {
    protected abstract val settingsKVPairs: List<ReplacementItem>

    fun getSpoofedValue(key: String) = settingsKVPairs.firstOrNull { it.name == key }
}
