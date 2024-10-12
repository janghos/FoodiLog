package com.foodilogg.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodilogg.activity.BaseActivity
import com.foodilogg.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.tvVersionNo.text = getAppVersionName(requireContext())
        binding.llPrivate.setOnClickListener {
            (requireActivity() as BaseActivity).replaceFragment(PrivateFragment())
        }
        return binding.root
    }

    private fun getAppVersionName(context: Context): String {
        try {
            val i = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = i.versionName.toString()
            return versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}