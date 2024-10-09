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
import com.foodilog.FoodilogApplication
import com.foodilog.PrefConstant
import com.foodilog.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val pref by lazy { FoodilogApplication.prefs }

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
        with(pref) {
            putFloat(PrefConstant.KEY_LAT, latitude.toFloat())
            putFloat(PrefConstant.KEY_LONG, longitude.toFloat())
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
            // 권한 요청 결과 확인
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되면 위치 정보를 가져옴
                getLastLocation()
            } else {
                // 권한이 거부되었을 경우
                commonDialogConfirm("위치 정보 필수 권한 필요합니다", "설정", "종료", { onMoveSetting() }, { appFinish() })
            }
        }
    }

    // 위치를 가져오는 메소드
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        saveLocationAndProceed(location)
                    } else {
                        // 위치가 null일 경우 예외 처리
                        commonDialogConfirm("위치 정보를 가져올 수 없습니다", "확인", "종료", {}, { appFinish() })
                    }
                }
                .addOnFailureListener {
                    // 위치 요청 실패 시 처리
                    commonDialogConfirm("위치 요청에 실패했습니다", "확인", "종료", {}, { appFinish() })
                }
        } else {
            // 권한이 없는 경우 권한 요청
            requestPermission()
        }
    }

    private fun appFinish() {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastLocation() // 권한이 있는 경우 위치를 가져옴
        }
    }
}
