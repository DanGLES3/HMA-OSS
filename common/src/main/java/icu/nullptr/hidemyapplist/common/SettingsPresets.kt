package icu.nullptr.hidemyapplist.common

import icu.nullptr.hidemyapplist.common.settings_presets.AccessibilityPreset
import icu.nullptr.hidemyapplist.common.settings_presets.BasePreset
import icu.nullptr.hidemyapplist.common.settings_presets.DeveloperOptionsPreset

class SettingsPresets private constructor() {
    private val presetList = mutableListOf<BasePreset>()

    companion object {
        val instance by lazy { SettingsPresets() }
    }

    fun getAllPresetNames() = presetList.map { it.name }.toTypedArray()
    // fun filterPresetsByName(names: Array<String>) = presetList.filter { names.contains(it.name) }
    fun getPresetByName(name: String) = presetList.firstOrNull { it.name == name }

    init {
        presetList.add(DeveloperOptionsPreset())
        presetList.add(AccessibilityPreset())
    }
}
