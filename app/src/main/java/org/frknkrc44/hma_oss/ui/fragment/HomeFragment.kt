package org.frknkrc44.hma_oss.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import icu.nullptr.hidemyapplist.hmaApp
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.service.ServiceClient
import icu.nullptr.hidemyapplist.ui.activity.AboutActivity
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.getColor
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.setCircleBackground
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.themeColor
import icu.nullptr.hidemyapplist.ui.util.makeToast
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
        setupToolbar(
            toolbar = binding.toolbar,
            title = getString(R.string.app_name),
            menuRes = R.menu.menu_about,
            onMenuOptionSelected = {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
            }
        )
    }

    @SuppressLint("StringFormatInvalid")
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
                val versionNameSimple = BuildConfig.VERSION_NAME.substringBefore(".r")
                moduleStatus.text =
                    getString(R.string.home_xposed_activated, versionNameSimple)
            } else {
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

        with(binding.howToUse) {
            text1.text = getString(R.string.about_how_to_use_title)
            itemIcon.setImageResource(R.drawable.outline_info_24)
            itemIcon.setCircleBackground(getColor(R.color.info))
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
            itemIcon.setImageResource(R.drawable.outline_android_24)
            itemIcon.setCircleBackground(getColor(R.color.warn))
        }

        with(binding.manageTemplates) {
            text1.text = getString(R.string.title_template_manage)
            itemIcon.setImageResource(R.drawable.ic_outline_layers_24)
            itemIcon.setCircleBackground(getColor(R.color.invalid))
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