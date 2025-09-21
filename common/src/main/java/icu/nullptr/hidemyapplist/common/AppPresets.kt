package icu.nullptr.hidemyapplist.common

import android.content.pm.ApplicationInfo
import android.content.pm.IPackageManager
import icu.nullptr.hidemyapplist.common.RiskyPackageUtils.appHasGMSConnection
import icu.nullptr.hidemyapplist.common.RiskyPackageUtils.ignoredForRiskyPackagesList
import icu.nullptr.hidemyapplist.common.Utils.getInstalledApplicationsCompat
import icu.nullptr.hidemyapplist.common.Utils.getPackageInfoCompat
import icu.nullptr.hidemyapplist.common.app_presets.BasePreset
import icu.nullptr.hidemyapplist.common.app_presets.CustomROMPreset
import icu.nullptr.hidemyapplist.common.app_presets.RootAppsPreset
import icu.nullptr.hidemyapplist.common.app_presets.SuspiciousAppsPreset
import icu.nullptr.hidemyapplist.common.app_presets.XposedModulesPreset

// TODO: Update presets when package added/removed
class AppPresets private constructor() {
    private val presetList = mutableListOf<BasePreset>()

    var loggerFunction: ((String) -> Unit)? = null

    companion object {
        val instance by lazy { AppPresets() }
    }

    fun getAllPresetNames() = presetList.map { it.name }.toTypedArray()
    // fun filterPresetsByName(names: Array<String>) = presetList.filter { names.contains(it.name) }
    fun getPresetByName(name: String) = presetList.firstOrNull { it.name == name }

    fun reloadPresetsIfEmpty(pms: IPackageManager) {
        val appsList = getInstalledApplicationsCompat(pms, 0, 0)

        for (appInfo in appsList) {
            runCatching {
                appHasGMSConnection(appInfo, appInfo.packageName, loggerFunction)
            }.onFailure { fail ->
                loggerFunction?.invoke(fail.toString())
            }

            presetList.forEach {
                runCatching {
                    it.addPackageInfoPreset(appInfo)
                }.onFailure { fail ->
                    loggerFunction?.invoke(fail.toString())
                }
            }
        }

        presetList.forEach {
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

        if (appInfo == null)
            appInfo = getPackageInfoCompat(pms, packageName, 0, 0).applicationInfo

        if (appInfo != null)
            appHasGMSConnection(appInfo, packageName, loggerFunction)

        if (addedInAList)
            loggerFunction?.invoke("Package add event handled for $packageName!")
    }

    fun handlePackageRemoved(packageName: String) {
        var itWasInAList = false

        presetList.forEach {
            if (it.removePackageFromPreset(packageName))
                itWasInAList = true
        }

        ignoredForRiskyPackagesList.remove(packageName)

        if (itWasInAList)
            loggerFunction?.invoke("Package remove event handled for $packageName!")
    }

    init {
        presetList.add(CustomROMPreset())
        presetList.add(RootAppsPreset())
        presetList.add(XposedModulesPreset())
        presetList.add(SuspiciousAppsPreset())
    }
}


