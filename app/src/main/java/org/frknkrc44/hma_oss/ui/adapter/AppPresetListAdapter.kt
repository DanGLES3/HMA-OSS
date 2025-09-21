package org.frknkrc44.hma_oss.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import icu.nullptr.hidemyapplist.common.AppPresets
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.ui.view.ListItemView
import org.frknkrc44.hma_oss.BuildConfig
import org.frknkrc44.hma_oss.R

class AppPresetListAdapter(
    context: Context,
    private val onClickListener: ((ConfigManager.PresetInfo) -> Unit)?
) : RecyclerView.Adapter<AppPresetListAdapter.ViewHolder>() {

    private lateinit var list: List<ConfigManager.PresetInfo>

    init {
        updateList(context)
    }

    inner class ViewHolder(view: ListItemView) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onClickListener?.invoke(list[absoluteAdapterPosition])
            }
        }

        fun bind(presetName: String) {
            with(itemView as ListItemView) {
                setIcon(R.drawable.baseline_assignment_24)
                text = presetName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListItemView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun getItemId(position: Int) = list[position].name.hashCode().toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position].translation)

    @SuppressLint("NotifyDataSetChanged", "DiscouragedApi")
    private fun updateList(context: Context) {
        val presetNames = AppPresets.instance.getAllPresetNames()
        val presetTranslations = presetNames.map { name ->
            try {
                val id = context.resources.getIdentifier(
                    "preset_${name}",
                    "string",
                    BuildConfig.APPLICATION_ID
                )

                return@map if (id != 0) { context.resources.getString(id) } else { name }
            } catch (_: Throwable) {}

            name
        }

        list = presetNames
            .map { ConfigManager.PresetInfo(it, presetTranslations[presetNames.indexOf(it)]) }
            .sortedWith { a, b -> a.translation.lowercase().compareTo(b.translation.lowercase()) }
        notifyDataSetChanged()
    }
}
