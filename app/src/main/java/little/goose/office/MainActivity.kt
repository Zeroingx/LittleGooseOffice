package little.goose.office

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.utils.viewBinding
import little.goose.design.system.theme.AccountTheme
import little.goose.home.data.*
import little.goose.home.ui.HomeScreen
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import little.goose.home.utils.withData
import little.goose.office.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                HomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
//        initView()
    }

    private fun initView() {
        initViewPager()
        initNavigation()
        recoverPager()
    }

    // 恢复到上次打开的页面
    private fun recoverPager() {
        lifecycleScope.launch {
            homeDataStore.withData(KEY_PREF_PAGER) { pager ->
                binding.homeViewPager.setCurrentItem(pager, false)
                binding.bottomNav.menu.getItem(pager).isChecked = true
            }
        }
    }

    private fun initViewPager() {
        binding.homeViewPager.apply {
            adapter = little.goose.home.ui.HomeFragmentPagerAdapter(this@MainActivity)
            isUserInputEnabled = false //禁止左右滑动
        }
    }

    override fun onStop() {
        super.onStop()
        // 保存当前页面
//        appScope.launch {
//            homeDataStore.edit { home ->
//                home[KEY_PREF_PAGER] = binding.homeViewPager.currentItem
//            }
//        }
    }

    private fun initNavigation() {
        binding.apply {
            bottomNav.itemIconTintList = null //Icon点击不变色
            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        homeViewPager.setCurrentItem(HOME, false)
                    }
                    R.id.nav_notebook -> {
                        homeViewPager.setCurrentItem(NOTEBOOK, false)
                    }
                    R.id.nav_account -> {
                        homeViewPager.setCurrentItem(ACCOUNT, false)
                    }
                    R.id.nav_schedule -> {
                        homeViewPager.setCurrentItem(SCHEDULE, false)
                    }
                    R.id.nav_memorial -> {
                        homeViewPager.setCurrentItem(MEMORIAL, false)
                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

}