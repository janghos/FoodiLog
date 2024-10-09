package com.foodilog.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foodilog.R
import com.foodilog.fragment.CommonDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun commonDialogAlert(string : String, function: Function<R>?){
        val commonDialogFragment = CommonDialogFragment()
        val bundle = Bundle().apply {
            putString("title", string)
            putBoolean("alert", true)
        }
        commonDialogFragment.arguments = bundle
        commonDialogFragment.setOnResultListener(object : CommonDialogFragment.OnResultListener {
            override fun onYes() {
                function
            }

            override fun onNo() {
                TODO("Not yet implemented")
            }

        })
        commonDialogFragment.show(supportFragmentManager, "alert")
    }

    fun commonDialogConfirm(title: String, okString : String?, noString : String?, function: () -> Unit, rejectFunction: () -> Unit) {
        val commonDialogFragment = CommonDialogFragment()
        val bundle = Bundle().apply {
            putString("title", title)
            okString?.let {
                putString("yes", it)
            }
            noString?.let {
                putString("no", it)
            }
        }
        commonDialogFragment.arguments = bundle
        commonDialogFragment.setOnResultListener(object : CommonDialogFragment.OnResultListener {
            override fun onYes() {
                function()  // 함수 실행
            }

            override fun onNo() {
                rejectFunction()  // 함수 실행
            }
        })
        commonDialogFragment.show(supportFragmentManager, "confirm")
    }
    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fm_container,fragment).addToBackStack(null).commit()
    }
    fun addFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().add(R.id.fm_container,fragment).addToBackStack(null).commit()
    }
}