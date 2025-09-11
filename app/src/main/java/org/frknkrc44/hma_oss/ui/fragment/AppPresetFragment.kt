package org.frknkrc44.hma_oss.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import icu.nullptr.hidemyapplist.service.PrefManager
import icu.nullptr.hidemyapplist.ui.fragment.AppSelectFragment
import icu.nullptr.hidemyapplist.util.PackageHelper
import icu.nullptr.hidemyapplist.util.PackageHelper.Comparators
import kotlinx.coroutines.launch
import org.frknkrc44.hma_oss.ui.adapter.AppPresetAdapter

class AppPresetFragment() : AppSelectFragment() {

    override val firstComparator = Comparator.comparing(PackageHelper::exists).reversed()

    override val adapter by lazy {
        val args by navArgs<AppPresetFragmentArgs>()
        AppPresetAdapter(args.presetName)
    }

    override fun getFragmentTitle(): String {
        val args by navArgs<AppPresetFragmentArgs>()
        return args.presetTitle
    }

    override fun sortList() {
        lifecycleScope.launch {
            sortPresetList()
            applyFilter()
        }
    }

    fun sortPresetList() {
        var comparator = when (PrefManager.appFilter_sortMethod) {
            PrefManager.SortMethod.BY_LABEL -> Comparators.byLabel
            PrefManager.SortMethod.BY_PACKAGE_NAME -> Comparators.byPackageName
            PrefManager.SortMethod.BY_INSTALL_TIME -> Comparators.byInstallTime
            PrefManager.SortMethod.BY_UPDATE_TIME -> Comparators.byUpdateTime
        }
        if (PrefManager.appFilter_reverseOrder) comparator = comparator.reversed()
        adapter.packages.sortWith(firstComparator.then(comparator))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        // returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
}