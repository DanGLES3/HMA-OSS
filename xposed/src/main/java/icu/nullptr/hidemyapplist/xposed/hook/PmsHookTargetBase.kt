package icu.nullptr.hidemyapplist.xposed.hook

import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.findMethodOrNull
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.XC_MethodHook
import icu.nullptr.hidemyapplist.common.Constants
import icu.nullptr.hidemyapplist.common.Constants.VENDING_PACKAGE_NAME
import icu.nullptr.hidemyapplist.common.Utils
import icu.nullptr.hidemyapplist.xposed.HMAService
import java.util.concurrent.atomic.AtomicReference

abstract class PmsHookTargetBase(protected val service: HMAService) : IFrameworkHook {
    protected val hooks = mutableListOf<XC_MethodHook.Unhook>()
    protected var lastFilteredApp: AtomicReference<String?> = AtomicReference(null)

    protected val psSigningInfo by lazy {
        Utils.getPackageInfoCompat(
            service.pms,
            VENDING_PACKAGE_NAME,
            PackageManager.GET_SIGNING_CERTIFICATES.toLong(),
            0
        ).signingInfo
    }

    abstract val fakeSystemPackageInstallInfo: Any?
    abstract val fakeUserPackageInstallInfo: Any?

    override fun load() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            findMethodOrNull(service.pms::class.java, findSuper = true) {
                name == "getInstallSourceInfo"
            }?.hookBefore { param ->
                val targetApp = param.args[0] as String?

                val callingUid = Binder.getCallingUid()
                if (callingUid == Constants.UID_SYSTEM) return@hookBefore
                val callingApps = Utils.binderLocalScope {
                    service.pms.getPackagesForUid(callingUid)
                } ?: return@hookBefore
                for (caller in callingApps) {
                    when (service.shouldHideInstallationSource(caller, targetApp)) {
                        1 -> param.result = fakeUserPackageInstallInfo
                        2 -> param.result = fakeSystemPackageInstallInfo
                        else -> continue
                    }

                    service.filterCount++
                    break
                }
            }?.let {
                hooks.add(it)
            }
        }

        hooks += findMethod(service.pms::class.java, findSuper = true) {
            name == "getInstallerPackageName"
        }.hookBefore { param ->
            val targetApp = param.args[0] as String?

            val callingUid = Binder.getCallingUid()
            if (callingUid == Constants.UID_SYSTEM) return@hookBefore
            val callingApps = Utils.binderLocalScope {
                service.pms.getPackagesForUid(callingUid)
            } ?: return@hookBefore
            for (caller in callingApps) {
                when (service.shouldHideInstallationSource(caller, targetApp)) {
                    1 -> param.result = VENDING_PACKAGE_NAME
                    2 -> param.result = null
                    else -> continue
                }

                service.filterCount++
                break
            }
        }
    }

    final override fun unload() {
        hooks.forEach(XC_MethodHook.Unhook::unhook)
        hooks.clear()
    }
}