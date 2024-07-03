package com.crc.ar

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crc.ar.base.Constants
import com.crc.ar.emergency.EmergencyActivity
import com.crc.ar.finedust.FineDustActivity
import com.crc.ar.gas.GasActivity
import com.crc.ar.gyro.GyroActivity
import com.crc.ar.setting.SettingActivity
import com.crc.ar.temperature.TemperatureActivity

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100

    lateinit var context: Context
    lateinit var mActivity: Activity

    private var REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    } else {
        TODO("VERSION.SDK_INT < S")
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    }

    lateinit private var getGPSPermissionLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_main_title)
//        tvToolbarTitle.setOnClickListener(this)

        context = applicationContext
        mActivity = this

        val mainIconList = arrayListOf<Int>(
            R.drawable.main_icon_finedust,
            R.drawable.main_icon_gas,
            R.drawable.main_icon_gyro,
//            R.drawable.main_icon_temperature,
            R.drawable.main_icon_set_up
        )

        val listAdapter = MainGridAdapter(this, mainIconList)

        val gvMainList : GridView = findViewById(R.id.gvMainList)
        gvMainList.adapter = listAdapter
        gvMainList.setOnItemClickListener(this)

        val preferences: SharedPreferences = getSharedPreferences(Constants.SHARED_PREF_SEUPDATA, Context.MODE_PRIVATE)

        Constants.strEmergencyNumber =
            preferences.getString(Constants.PREF_EMERGENCY_CALL_NUMBER, Constants.strEmergencyNumber).toString()


//        var commonUtils = CommonUtils()
//        var curDate = commonUtils.getCurrentDate()
//
//        Constants.curYearOfData = curDate[0].toInt()
//        Constants.curMonthOfData = curDate[1].toInt()
//        Constants.curDayOfData = curDate[2].toInt()
//        Constants.curHourOfData = curDate[3].toInt()
//        Constants.curMinuteOfData = curDate[4].toInt()
//        Constants.curSecondOfData = curDate[5].toInt()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //퍼미션 상태 확인
//            if (!hasPermissions(PERMISSIONS)) {
//                //퍼미션 허가 안되어있다면 사용자에게 요청
//                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE)
//            }
//        }
        checkAllPermissions()
    }

    private fun checkAllPermissions() {
        if(!isLocationServicesAvailable()) {
            showDialogForLocationServiceSetting()
        } else {
            isRunTimePermissionsGranted()
        }
    }

    private fun isLocationServicesAvailable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER))
    }

    private fun isRunTimePermissionsGranted() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            for(result in grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }

            if(!checkResult) {
                Toast.makeText(this@MainActivity, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                result ->
            if(result.resultCode == Activity.RESULT_OK) {
                if(isLocationServicesAvailable()) {
                    isRunTimePermissionsGranted()
                } else {
                    Toast.makeText(this@MainActivity, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        val builder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져 있습니다. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener {
                dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener{
                dialog, id ->
            dialog.cancel()
            Toast.makeText(this@MainActivity, "기기에서 위치서비스(GPS) 설정 후 사용 해 주세요.", Toast.LENGTH_SHORT).show()
            finish()
        })
        builder.create().show()
    }


//    private fun hasPermissions(permissions: Array<String>): Boolean {
//        var result: Int
//        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
//        for (perms in permissions) {
//            result = ContextCompat.checkSelfPermission(this, perms)
//            if (result == PackageManager.PERMISSION_DENIED) {
//                //허가 안된 퍼미션 발견
//                return false
//            }
//        }
//        //모든 퍼미션이 허가되었음
//        return true
//    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.e("eleutheria", "position : " + p2)
        when (p2) {
            0 -> { // finedust
                Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_FINEDUST

                val activityIntent : Intent = Intent(this, FineDustActivity::class.java)
//                val activityIntent = Intent(this, LoadingActivity::class.java)
                startActivity(activityIntent)
            }
            1 -> { // gas
                Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_GAS

                val activityIntent : Intent = Intent(this, GasActivity::class.java)
//                val activityIntent = Intent(this, LoadingActivity::class.java)
                startActivity(activityIntent)
            }
            2 -> { // gyro
                Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_EMERGENCY

                val activityIntent : Intent = Intent(this, EmergencyActivity::class.java)
//                val activityIntent = Intent(this, LoadingActivity::class.java)
                startActivity(activityIntent)
            }
//            3 -> { // temperature, humidity
//                Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_TEMPERATURE
//
//                val activityIntent : Intent = Intent(this, TemperatureActivity::class.java)
////                val activityIntent = Intent(this, LoadingActivity::class.java)
//                startActivity(activityIntent)
//            }
            3 -> { // setup
                Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_SETTING

                val activityIntent = Intent(this, SettingActivity::class.java)
                startActivity(activityIntent)
            }
        }
    }
}