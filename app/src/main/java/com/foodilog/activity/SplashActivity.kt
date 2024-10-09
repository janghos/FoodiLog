package com.foodilog.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.foodilog.PrefConstant
import com.foodilog.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SplashActivity : BaseActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()
    }

    override fun onRestart() {
        super.onRestart()
        requestPermission()
    }

    private fun saveLocationAndProceed(location: Location) {
        // 실제 위치 정보를 사용
        val latitude = location.latitude
        val longitude = location.longitude

        // SharedPreferences에 위치 정보 저장
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putFloat(PrefConstant.KEY_LAT, latitude.toFloat())
            putFloat(PrefConstant.KEY_LONG, longitude.toFloat())
            apply()
        }

        // 3초 뒤에 메인 액티비티로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                commonDialogConfirm("위치 정보 필수 권한 필요합니다", "설정", "종료" , { onMoveSetting() },{ appFinish() })
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            saveLocationAndProceed(it)
                        } ?: run {
                            // 위치가 null일 경우 예외 처리
                            commonDialogConfirm("위치 정보를 가져올 수 없습니다", "확인", "종료", {}, { appFinish() })
                        }
                    }
            }
        }
    }

    private fun appFinish(){
        finish()
    }
    private fun onMoveSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun requestPermission() {
        // 위치 권한 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        saveLocationAndProceed(it)
                    } ?: run {
                        // 위치가 null일 경우 예외 처리
                        commonDialogConfirm("위치 정보를 가져올 수 없습니다", "확인", "종료", {}, { appFinish() })
                    }
                }
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        saveLocationAndProceed(it)
                    } ?: run {
                        // 위치가 null일 경우 예외 처리
                        commonDialogConfirm("위치 정보를 가져올 수 없습니다", "확인", "종료", {}, { appFinish() })
                    }
                }
        }
    }
}