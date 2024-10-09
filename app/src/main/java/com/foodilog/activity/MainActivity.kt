package com.foodilog.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foodilog.R
import com.foodilog.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.home_fragment -> {

                    true
                }

                R.id.setting_fragment -> {
                    true
                }

                R.id.history_fragment -> {

                    true
                }
            }
            false
        }
    }
}