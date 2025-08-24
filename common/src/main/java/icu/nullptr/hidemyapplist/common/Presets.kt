package icu.nullptr.hidemyapplist.common

import android.content.pm.ApplicationInfo
import android.content.pm.IPackageManager
import icu.nullptr.hidemyapplist.common.Utils.getInstalledApplicationsCompat
import icu.nullptr.hidemyapplist.common.Utils.getPackageInfoCompat
import icu.nullptr.hidemyapplist.common.presets.BasePreset
import icu.nullptr.hidemyapplist.common.presets.CustomROMPreset
import icu.nullptr.hidemyapplist.common.presets.RootAppsPreset
import icu.nullptr.hidemyapplist.common.presets.XposedModulesPreset

// TODO: Update presets when package added/removed
class Presets private constructor() {
    val presetList = mutableListOf<BasePreset>()
    var loggerFunction: ((String) -> Unit)? = null

    companion object {
        private var hiddenInstance: Presets? = null

        val instance: Presets
            get() {
                if (hiddenInstance == null) {
                    hiddenInstance = Presets()
                }

                return hiddenInstance!!
            }
    }

    fun getAllPresetNames() = presetList.map { it.name }.toTypedArray()
    // fun filterPresetsByName(names: Array<String>) = presetList.filter { names.contains(it.name) }
    fun getPresetByName(name: String) = presetList.firstOrNull { it.name == name }

    fun reloadPresetsIfEmpty(pms: IPackageManager) {
        var appsList: List<ApplicationInfo>? = null

        presetList.forEach {
            if (it.isDynamicListEmpty()) {
                if (appsList == null) {
                    appsList = getInstalledApplicationsCompat(pms, 0, 0)
                }

                for (appInfo in appsList) {
                    runCatching {
                        it.addPackageInfoPreset(appInfo)
                    }.onFailure { fail ->
                        loggerFunction?.invoke(fail.toString())
                    }
                }
            }

            loggerFunction?.invoke(it.toString())
        }
    }

    fun handlePackageAdded(pms: IPackageManager, packageName: String) {
        var appInfo: ApplicationInfo? = null
        var addedInAList = false

        presetList.forEach {
            if (!it.containsPackage(packageName)) {
                if (appInfo == null)
                    appInfo = getPackageInfoCompat(pms, packageName, 0, 0).applicationInfo

                if (appInfo != null) {
                    runCatching {
                        if (it.addPackageInfoPreset(appInfo!!)) {
                            loggerFunction?.invoke("Package $packageName added into ${it.name}!")
                            addedInAList = true
                        }
                    }.onFailure { fail ->
                        loggerFunction?.invoke(fail.toString())
                    }
                }
            }
        }

        if (addedInAList)
            loggerFunction?.invoke("Package add event handled for $packageName!")
    }

    fun handlePackageRemoved(packageName: String) {
        var itWasInAList = false

        presetList.forEach {
            if (it.removePackageFromPreset(packageName))
                itWasInAList = true
        }

        if (itWasInAList)
            loggerFunction?.invoke("Package remove event handled for $packageName!")
    }

    init {
        presetList.add(CustomROMPreset())
        presetList.add(RootAppsPreset())
        presetList.add(XposedModulesPreset())
    }
}


