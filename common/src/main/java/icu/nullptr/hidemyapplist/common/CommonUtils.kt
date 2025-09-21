package icu.nullptr.hidemyapplist.common

import android.os.SystemProperties

object CommonUtils {
    const val XPOSED_PROP1 = "\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000m\u0000o\u0000d\u0000u\u0000l\u0000e"
    const val XPOSED_PROP2 = "\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000s\u0000c\u0000o\u0000p\u0000e"

    val isAppDataIsolationEnabled: Boolean
        get() = SystemProperties.getBoolean(Constants.ANDROID_APP_DATA_ISOLATION_ENABLED_PROPERTY, true)

    val isVoldAppDataIsolationEnabled: Boolean
        get() = SystemProperties.getBoolean(Constants.ANDROID_VOLD_APP_DATA_ISOLATION_ENABLED_PROPERTY, false)
}
