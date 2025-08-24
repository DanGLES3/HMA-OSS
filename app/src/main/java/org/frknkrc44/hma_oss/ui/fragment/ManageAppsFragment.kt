package org.frknkrc44.hma_oss.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.ui.util.navController
import icu.nullptr.hidemyapplist.util.PackageHelper
import kotlinx.coroutines.launch
import org.frknkrc44.hma_oss.R
import org.frknkrc44.hma_oss.databinding.FragmentManageAppsBinding
import org.frknkrc44.hma_oss.ui.adapter.AppListAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ManageAppsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageAppsFragment : Fragment(R.layout.fragment_manage_apps) {
    protected val firstComparator: Comparator<String> = Comparator.comparing(ConfigManager::isHideEnabled).reversed()

    private val binding by viewBinding<FragmentManageAppsBinding>()
    val adapter: AppListAdapter

    private var search = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected open fun onBack() {
        navController.navigateUp()
    }

    private fun applyFilter() {
        adapter.filter.filter(search)
    }

    private fun sortList() {
        lifecycleScope.launch {
            PackageHelper.sortList(firstComparator)
            applyFilter()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = ManageAppsFragment()
    }
}