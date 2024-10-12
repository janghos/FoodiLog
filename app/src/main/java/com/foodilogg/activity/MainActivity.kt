package com.foodilogg.activity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.foodilogg.R
import com.foodilogg.databinding.ActivityMainBinding
import com.foodilogg.fragment.HistoryFragment
import com.foodilogg.fragment.HomeFragment
import com.foodilogg.fragment.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.home_fragment -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.history_fragment -> {
                    replaceFragment(HistoryFragment())
                    true
                }

                R.id.setting_fragment -> {
                    replaceFragment(SettingFragment())
                    true
                }
            }
            false
        }
    }

    fun goneNav() {
        binding.bottomNav.visibility = View.GONE
        val layoutParams = binding.fmContainer.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = 0
        binding.fmContainer.layoutParams = layoutParams
    }

    fun visibleNav() {
        binding.bottomNav.visibility = View.VISIBLE
        val layoutParams = binding.fmContainer.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_height)
        binding.fmContainer.layoutParams = layoutParams
    }
}