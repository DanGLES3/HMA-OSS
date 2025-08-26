package icu.nullptr.hidemyapplist.xposed.hook

import android.content.Intent
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticIntField
import icu.nullptr.hidemyapplist.xposed.HMAService
import icu.nullptr.hidemyapplist.xposed.logE
import icu.nullptr.hidemyapplist.xposed.logI

class ActivityHook(private val service: HMAService) : IFrameworkHook {
    companion object {
        private const val TAG = "ActivityHook"
    }

    private var hook: XC_MethodHook.Unhook? = null

    override fun load() {
        logI(TAG, "Load hook")

        hook = findMethod(
            "com.android.server.wm.ActivityStarter"
        ) {
            name == "execute"
        }.hookBefore { param ->
            runCatching {
                val request = getObjectField(param.thisObject, "mRequest")
                val caller = getObjectField(request, "callingPackage") as String?
                val intent = getObjectField(request, "intent") as Intent?
                val targetApp = intent?.component?.packageName

                if (service.shouldHide(caller, targetApp)) {
                    logI(
                        TAG,
                        "@executeRequest: insecure query from $caller, target: ${intent?.component}"
                    )
                    param.result = getStaticIntField(
                        findClass(
                            "android.app.ActivityManager",
                            InitFields.ezXClassLoader
                        ),
                        "START_INTENT_NOT_RESOLVED"
                    )
                }
            }.onFailure {
                logE(TAG, "Fatal error occurred, ignore hook\n", it)
                // unload()
            }
        }
    }

    override fun unload() {
        hook?.unhook()
        hook = null
    }
}