package icu.nullptr.hidemyapplist.common.settings_presets

data class ReplacementItem(
    val name: String,
    val value: String?
) {
    override fun toString() = "ReplacementItem {" +
            " 'name': '$name'," +
            "'value': '$value'" +
            " }"
}