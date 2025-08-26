package icu.nullptr.hidemyapplist.ui.fragment

import android.os.Bundle
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.ui.adapter.AppManageAdapter
import icu.nullptr.hidemyapplist.ui.util.navController
import org.frknkrc44.hma_oss.R

class AppManageFragment : AppSelectFragment() {

    override val firstComparator: Comparator<String> = Comparator.comparing(ConfigManager::isHideEnabled).reversed()

    override val adapter = AppManageAdapter {
        val args = AppSettingsFragmentArgs(it)
        navController.navigate(R.id.nav_app_settings, args.toBundle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        // returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
}
