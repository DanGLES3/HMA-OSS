package org.frknkrc44.hma_oss.ui.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import icu.nullptr.hidemyapplist.hmaApp
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.service.ServiceClient
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.attrDrawable
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.getColor
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.homeItemBackgroundColor
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.themeColor
import icu.nullptr.hidemyapplist.ui.util.makeToast
import icu.nullptr.hidemyapplist.ui.util.navigate
import icu.nullptr.hidemyapplist.ui.util.setupToolbar
import org.frknkrc44.hma_oss.BuildConfig
import org.frknkrc44.hma_oss.R
import org.frknkrc44.hma_oss.databinding.FragmentHomeBinding
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding<FragmentHomeBinding>()

    private val backupSAFLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) backup@{ uri ->
            if (uri == null) return@backup
            ConfigManager.configFile.inputStream().use { input ->
                hmaApp.contentResolver.openOutputStream(uri).use { output ->
                    if (output == null) makeToast(R.string.home_export_failed)
                    else input.copyTo(output)
                }
            }
            makeToast(R.string.home_exported)
        }

    private val restoreSAFLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) restore@{ uri ->
            if (uri == null) return@restore
            runCatching {
                val backup = hmaApp.contentResolver
                    .openInputStream(uri)?.reader().use { it?.readText() }
                    ?: throw IOException(getString(R.string.home_import_file_damaged))
                ConfigManager.importConfig(backup)
                makeToast(R.string.home_import_successful)
            }.onFailure {
                it.printStackTrace()
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle(R.string.home_import_failed)
                    .setMessage(it.message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(R.string.show_crash_log) { _, _ ->
                        MaterialAlertDialogBuilder(requireActivity())
                            .setCancelable(false)
                            .setTitle(R.string.home_import_failed)
                            .setMessage(it.stackTraceToString())
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                    .show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.toolbar) {
            setupToolbar(
                toolbar = binding.toolbar,
                title = getString(R.string.app_name),
            )
            isTitleCentered = true
        }
    }

    @SuppressLint("StringFormatInvalid", "StringFormatMatches")
    override fun onStart() {
        super.onStart()

        val serviceVersion = ServiceClient.serviceVersion
        val color = when {
            !hmaApp.isHooked -> getColor(R.color.gray)
            serviceVersion == 0 -> getColor(R.color.invalid)
            else -> themeColor(android.R.attr.colorPrimary)
        }

        with(binding.statusCard) {
            root.setCardBackgroundColor(color)
            root.outlineAmbientShadowColor = color
            root.outlineSpotShadowColor = color

            if (hmaApp.isHooked) {
                moduleStatusIcon.setImageResource(R.drawable.sentiment_calm_24px)
                val versionNameSimple = BuildConfig.VERSION_NAME.substringBefore(".r")
                moduleStatus.text =
                    getString(R.string.home_xposed_activated, versionNameSimple)
            } else {
                moduleStatusIcon.setImageResource(R.drawable.sentiment_very_dissatisfied_24px)
                moduleStatus.setText(R.string.home_xposed_not_activated)
            }

            if (serviceVersion != 0) {
                if (serviceVersion < org.frknkrc44.hma_oss.common.BuildConfig.SERVICE_VERSION) {
                    serviceStatus.text =
                        getString(R.string.home_xposed_service_old)
                } else {
                    serviceStatus.text =
                        getString(R.string.home_xposed_service_on, serviceVersion)
                }
                filterCount.visibility = View.VISIBLE
                filterCount.text =
                    getString(R.string.home_xposed_filter_count, ServiceClient.filterCount)
            } else {
                serviceStatus.setText(R.string.home_xposed_service_off)
                filterCount.visibility = View.GONE
            }
        }

        with(binding.howToUse.root.parent as ViewGroup) {
            val childCount = childCount

            val softCorner: Float = resources.displayMetrics.density * 24
            val squareCorner: Float = resources.displayMetrics.density * 8
            val pad = (resources.displayMetrics.density * 16).toInt()

            for (i in 0..< childCount) {
                getChildAt(i).apply {
                    (this as ViewGroup).apply {
                        findViewById<TextView>(android.R.id.text1).setTextColor(
                            themeColor(
                                com.google.android.material.R.attr.colorOnSurface,
                            ),
                        )

                        findViewById<ImageView>(android.R.id.icon).setColorFilter(
                            themeColor(
                                com.google.android.material.R.attr.colorOnSurface,
                            ),
                        )
                    }

                    (layoutParams as LinearLayout.LayoutParams).apply {
                        if (i == 0) {
                            setMargins(pad, pad, pad, 0)
                        } else if (i == childCount - 1) {
                            setMargins(pad, 0, pad, pad)
                        } else {
                            setMargins(pad, 0, pad, 0)
                        }
                    }

                    val backgroundDrawable = GradientDrawable()
                    backgroundDrawable.setColor(homeItemBackgroundColor())

                    if (i == 0) {
                        backgroundDrawable.setCornerRadii(
                            floatArrayOf(
                                softCorner,
                                softCorner,
                                softCorner,
                                softCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner
                            )
                        )
                    } else if (i == childCount - 1) {
                        backgroundDrawable.setCornerRadii(
                            floatArrayOf(
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                softCorner,
                                softCorner,
                                softCorner,
                                softCorner
                            )
                        )
                    } else {
                        backgroundDrawable.setCornerRadii(
                            floatArrayOf(
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner,
                                squareCorner
                            )
                        )
                    }

                    val ripple = attrDrawable(android.R.attr.selectableItemBackground)
                    val layerDrawable = LayerDrawable(arrayOf(
                        backgroundDrawable,
                        ripple,
                    ))

                    background = layerDrawable
                    clipToOutline = true
                }

            }
        }

        with(binding.howToUse) {
            text1.text = getString(R.string.about_how_to_use_title)
            icon.setImageResource(R.drawable.outline_info_24)
            root.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.about_how_to_use_title)
                    .setMessage(
                        getString(R.string.about_how_to_use_description_1) +
                                "\n\n" +
                                getString(R.string.about_how_to_use_description_2))
                    .setNegativeButton(android.R.string.ok, null)
                    .show()
            }
        }

        with(binding.manageApps) {
            text1.text = getString(R.string.title_app_manage)
            icon.setImageResource(R.drawable.outline_android_24)
            root.setOnClickListener {
                navigate(R.id.nav_app_manage)
            }
        }

        with(binding.manageTemplates) {
            text1.text = getString(R.string.title_template_manage)
            icon.setImageResource(R.drawable.ic_outline_layers_24)
            root.setOnClickListener {
                navigate(R.id.nav_template_manage)
            }
        }

        with(binding.navLogs) {
            text1.text = getString(R.string.title_logs)
            icon.setImageResource(R.drawable.outline_assignment_24)
            root.setOnClickListener {
                navigate(R.id.nav_logs)
            }
        }

        binding.backupConfig.setOnClickListener {
            backupSAFLauncher.launch("HMA_Config_${System.currentTimeMillis()}.json")
        }

        binding.restoreConfig.setOnClickListener {
            restoreSAFLauncher.launch("application/json")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}