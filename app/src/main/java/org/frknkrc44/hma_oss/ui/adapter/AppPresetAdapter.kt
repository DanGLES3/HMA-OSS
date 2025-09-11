package org.frknkrc44.hma_oss.ui.adapter

import android.view.ViewGroup
import android.widget.Filter
import icu.nullptr.hidemyapplist.service.ServiceClient
import icu.nullptr.hidemyapplist.ui.adapter.AppSelectAdapter
import icu.nullptr.hidemyapplist.ui.view.AppItemView
import icu.nullptr.hidemyapplist.util.PackageHelper
import kotlinx.coroutines.runBlocking

class AppPresetAdapter(
    private val presetName: String
) : AppSelectAdapter() {
    val packages by lazy { ServiceClient.getPackagesForPreset(presetName)?.toMutableList() ?: mutableListOf() }

    inner class ViewHolder(view: AppItemView) : AppSelectAdapter.ViewHolder(view) {
        override fun bind(packageName: String) {
            (itemView as AppItemView).let {
                it.load(packageName)
                it.alpha = if (!PackageHelper.exists(packageName)) 0.5f else 1.0f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppItemView(parent.context, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }

    private inner class PresetFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            return runBlocking {
                val constraintLowered = constraint.toString().lowercase()
                val filteredList = packages.filter {
                    if (constraintLowered.isEmpty()) return@filter true
                    val label = PackageHelper.loadAppLabel(it)
                    label.lowercase().contains(constraintLowered) || it.lowercase().contains(constraintLowered)
                }

                FilterResults().also { it.values = filteredList }
            }
        }

        @Suppress("UNCHECKED_CAST", "NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredList = results.values as List<String>
            notifyDataSetChanged()
        }
    }

    private val mFilter = PresetFilter()

    override fun getFilter(): Filter = mFilter
}
