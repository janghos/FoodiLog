package com.foodilog.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foodilog.R
import com.foodilog.databinding.ActivityMainBinding
import com.foodilog.fragment.HistoryFragment
import com.foodilog.fragment.HomeFragment
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
                    true
                }
            }
            false
        }
    }
}