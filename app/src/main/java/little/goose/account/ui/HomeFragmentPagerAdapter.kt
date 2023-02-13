package little.goose.account.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import little.goose.account.logic.data.constant.COUNT
import little.goose.account.ui.account.AccountFragment
import little.goose.account.ui.home.HomeFragment
import little.goose.memorial.ui.MemorialFragment
import little.goose.note.ui.NotebookFragment
import little.goose.schedule.ui.ScheduleFragment

class HomeFragmentPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> NotebookFragment.newInstance()
            2 -> AccountFragment.newInstance()
            3 -> ScheduleFragment.newInstance()
            4 -> MemorialFragment.newInstance()
            else -> throw Exception("Not support this position")
        }
    }

}