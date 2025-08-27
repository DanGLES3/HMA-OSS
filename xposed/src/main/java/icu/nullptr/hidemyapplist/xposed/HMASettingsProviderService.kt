package icu.nullptr.hidemyapplist.xposed

import android.content.ContentProvider
import android.os.Bundle
import android.provider.Settings
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import icu.nullptr.hidemyapplist.common.JsonConfig
import icu.nullptr.hidemyapplist.common.SettingsPresets
import icu.nullptr.hidemyapplist.common.Utils
import icu.nullptr.hidemyapplist.common.settings_presets.ReplacementItem
import org.frknkrc44.hma_oss.common.BuildConfig
import java.io.File

class HMASettingsProviderService : IXposedHookLoadPackage {
    companion object {
        private const val TAG = "SettingsProviderHook"
    }

    init {
        searchDataDir()
    }

    private lateinit var dataDir: String
    private lateinit var configFile: File

    private val configLock = Any()
    private var configLastModified: Long = 0

    var config = JsonConfig().apply { detailLog = true }
        private set

    private fun searchDataDir() {
        File("/data/system").list()?.forEach {
            if (it.startsWith("hide_my_applist")) {
                if (!this::dataDir.isInitialized) {
                    val newDir = File("/data/misc/$it")
                    File("/data/system/$it").renameTo(newDir)
                    dataDir = newDir.path
                } else {
                    File("/data/system/$it").deleteRecursively()
                }
            }
        }
        File("/data/misc").list()?.forEach {
            if (it.startsWith("hide_my_applist")) {
                if (!this::dataDir.isInitialized) {
                    dataDir = "/data/misc/$it"
                } else if (dataDir != "/data/misc/$it") {
                    File("/data/misc/$it").deleteRecursively()
                }
            }
        }
        if (!this::dataDir.isInitialized) {
            dataDir = "/data/misc/hide_my_applist_" + Utils.generateRandomString(16)
        }

        File("$dataDir/log").mkdirs()
        configFile = File("$dataDir/config.json")

        logI(TAG, "Data dir: $dataDir")
    }

    private fun loadConfig() {
        if (!configFile.exists()) {
            logI(TAG, "Config file not found")
            return
        }

        if (configLastModified == configFile.lastModified()) {
            return
        }

        configLastModified = configFile.lastModified()

        val loading = runCatching {
            val json = configFile.readText()
            JsonConfig.parse(json)
        }.getOrElse {
            logE(TAG, "Failed to parse config.json", it)
            return
        }
        if (loading.configVersion != BuildConfig.CONFIG_VERSION) {
            logW(TAG, "Config version mismatch, need to reload")
            return
        }
        config = loading
        logI(TAG, "Config loaded")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        logI(TAG, "Load hook")

        findMethod(
            "com.android.providers.settings.SettingsProvider",
            lpparam.classLoader,
        ) {
            name == "call"
        }.hookBefore { param ->
            loadConfig()

            val contentProvider = param.thisObject as ContentProvider
            val method = param.args[0] as String?
            val name = param.args[1] as String?

            val caller = contentProvider.callingPackage
            if (!config.scope.containsKey(caller)) {
                // logD(TAG, "Not enabled app: $caller")
                return@hookBefore
            }

            logD(TAG, "@call caller: $caller, method: $method, name: $name")

            when (method) {
                "GET_global", "GET_secure", "GET_system" -> {
                    val replacement = getSpoofedSetting(caller, name)
                    if (replacement != null) {
                        logD(TAG, "@getSettings returned replacement for $caller: ${replacement.value}")
                        param.result = Bundle().apply {
                            putString(Settings.NameValueTable.VALUE, replacement.value)
                        }
                    }
                }
            }
        }
    }

    fun getSpoofedSetting(caller: String?, name: String?): ReplacementItem? {
        synchronized(configLock) {
            if (caller == null || name == null) return null

            val presets = config.scope[caller]?.applySettingsPresets
            if (presets?.isEmpty() ?: true) {
                return null
            }

            for (presetName in presets) {
                val preset = SettingsPresets.instance.getPresetByName(presetName)
                val value = preset?.getSpoofedValue(name)
                if (value != null) return value
            }

            return null
        }
    }
}