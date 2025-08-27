package icu.nullptr.hidemyapplist.xposed.hook

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityThread
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.XC_MethodHook
import icu.nullptr.hidemyapplist.common.settings_presets.AccessibilityPreset
import icu.nullptr.hidemyapplist.xposed.HMAService
import icu.nullptr.hidemyapplist.xposed.logE
import icu.nullptr.hidemyapplist.xposed.logI

class AccessibilityHook(private val service: HMAService) : IFrameworkHook {
    companion object {
        private const val TAG = "AccessibilityHook"
    }

    private val hookList = mutableSetOf<XC_MethodHook.Unhook>()

    override fun load() {
        logI(TAG, "Load hook")

        hookList += findMethod(
            "com.android.server.accessibility.AccessibilityManagerService"
        ) {
            name == "getInstalledAccessibilityServiceList"
        }.hookBefore { param -> hookedMethod(param) }

        hookList += findMethod(
            "com.android.server.accessibility.AccessibilityManagerService"
        ) {
            name == "getEnabledAccessibilityServiceList"
        }.hookBefore { param -> hookedMethod(param) }
    }

    private fun hookedMethod(param: XC_MethodHook.MethodHookParam) {
        try {
            val caller = ActivityThread.currentActivityThread().application.packageName
            if (service.getEnabledSettingsPresets(caller).contains(AccessibilityPreset.NAME)) {
                param.result = java.util.ArrayList<AccessibilityServiceInfo>()
            }
        } catch (e: Throwable) {
            logE(TAG, "Fatal error occurred, ignore hooks", e)
        }
    }

    override fun unload() {
        hookList.forEach { it.unhook() }
        hookList.clear()
    }
}