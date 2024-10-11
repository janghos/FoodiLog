package com.foodilog.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.foodilog.R
import com.foodilog.fragment.CommonDialogFragment
import com.foodilog.shop.ShopViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    private var backToExit = false
    lateinit var shopViewModel: ShopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)
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

    override fun onBackPressed() {
        if (backToExit) {
            super.onBackPressed()
            finish()
        }

        this.backToExit = true
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            backToExit = false
        }, 2000)
    }
}