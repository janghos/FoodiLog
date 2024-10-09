package com.foodilog.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foodilog.R
import com.foodilog.fragment.CommonDialogFragment

class BaseActivity : AppCompatActivity() {
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

    fun commonDialogConfirm(string : String, function: Function<R>){
        val commonDialogFragment = CommonDialogFragment()
        val bundle = Bundle().apply {
            putString("title", string)
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
        commonDialogFragment.show(supportFragmentManager, "confirm")
    }
}