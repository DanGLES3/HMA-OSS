package icu.nullptr.hidemyapplist.util

import icu.nullptr.hidemyapplist.service.PrefManager
import java.util.Locale

class ConfigUtils private constructor() {
    companion object {
        fun getLocale(): Locale {
            val tag = PrefManager.locale
            return if (tag == "SYSTEM") Locale.getDefault()
            else Locale.forLanguageTag(tag)
        }
    }
}