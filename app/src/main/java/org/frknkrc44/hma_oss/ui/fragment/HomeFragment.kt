package org.frknkrc44.hma_oss.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import by.kirich1409.viewbindingdelegate.viewBinding
import icu.nullptr.hidemyapplist.hmaApp
import icu.nullptr.hidemyapplist.service.ServiceClient
import icu.nullptr.hidemyapplist.ui.activity.AboutActivity
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.getColor
import icu.nullptr.hidemyapplist.ui.util.ThemeUtils.themeColor
import icu.nullptr.hidemyapplist.ui.util.setupToolbar
import org.frknkrc44.hma_oss.BuildConfig
import org.frknkrc44.hma_oss.R
import org.frknkrc44.hma_oss.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding<FragmentHomeBinding>()

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
        binding.statusCard.setCardBackgroundColor(color)
        binding.statusCard.outlineAmbientShadowColor = color
        binding.statusCard.outlineSpotShadowColor = color

        if (hmaApp.isHooked) {
            val versionNameSimple = BuildConfig.VERSION_NAME.substringBefore(".r")
            binding.appVersion.text = getString(R.string.home_xposed_activated, versionNameSimple, BuildConfig.VERSION_CODE)
        } else {
            binding.appVersion.setText(R.string.home_xposed_not_activated)
        }

        if (serviceVersion != 0) {
            if (serviceVersion < org.frknkrc44.hma_oss.common.BuildConfig.SERVICE_VERSION) {
                binding.serviceStatus.text = getString(R.string.home_xposed_service_old)
            } else {
                binding.serviceStatus.text = getString(R.string.home_xposed_service_on, serviceVersion)
            }
            binding.filterCount.visibility = View.VISIBLE
            binding.filterCount.text = getString(R.string.home_xposed_filter_count, ServiceClient.filterCount)
        } else {
            binding.serviceStatus.setText(R.string.home_xposed_service_off)
            binding.filterCount.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}