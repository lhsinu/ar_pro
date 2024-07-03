package com.crc.ar

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.crc.ar.base.Constants
import com.crc.ar.databinding.ActivitySettingDeviceBinding

class SettingDeviceActivity : AppCompatActivity() {

    private  lateinit var binding : ActivitySettingDeviceBinding
    private var settings: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.strSettingDeviceTitle)

        settings = getSharedPreferences(Constants.SHARED_PREF_DEVICEDATA, Context.MODE_PRIVATE)

//        String data1=sharedPref.getString("data1", "none");
        val strGasData : String? = settings!!.getString(Constants.PREF_GAS_DEVICE, Constants.MODULE_ADDRESS_GAS)
//        val strGyroData : String? = settings!!.getString(Constants.PREF_GYRO_DEVICE, Constants.MODULE_ADDRESS_GYRO)

        if (strGasData != null) {
            val arDrivingData = strGasData.split(":")

            if(arDrivingData.size > 5) {
                binding.etGas1.setText(arDrivingData[0])
                binding.etGas2.setText(arDrivingData[1])
                binding.etGas3.setText(arDrivingData[2])
                binding.etGas4.setText(arDrivingData[3])
                binding.etGas5.setText(arDrivingData[4])
                binding.etGas6.setText(arDrivingData[5])
            }
        }

//        if (strGyroData != null) {
//            val arFrontData = strGyroData.split(":")
//
//            if(arFrontData.size > 5) {
//                binding.etGyro1.setText(arFrontData[0])
//                binding.etGyro2.setText(arFrontData[1])
//                binding.etGyro3.setText(arFrontData[2])
//                binding.etGyro4.setText(arFrontData[3])
//                binding.etGyro5.setText(arFrontData[4])
//                binding.etGyro6.setText(arFrontData[5])
//            }
//        }

        binding.btGasOk.setOnClickListener {
            val strGasAddress = binding.etGas1.text.toString() + ":" + binding.etGas2.text.toString() + ":" + binding.etGas3.text.toString() + ":" + binding.etGas4.text.toString() + ":" + binding.etGas5.text.toString() + ":" + binding.etGas6.text.toString()

            Log.e("eleutheria", "strGasAddress : $strGasAddress")
            val editor = settings!!.edit()
            editor.putString(Constants.PREF_GAS_DEVICE, strGasAddress)
            editor.apply()

            Toast.makeText(this, "GasAddress : $strGasAddress", Toast.LENGTH_SHORT).show()
        }


        binding.btGyroOk.setOnClickListener {
            val strGyroAddress = binding.etGyro1.text.toString() + ":" + binding.etGyro2.text.toString() + ":" + binding.etGyro3.text.toString() + ":" + binding.etGyro4.text.toString() + ":" + binding.etGyro5.text.toString() + ":" + binding.etGyro6.text.toString()

            Log.e("eleutheria", "strGyroAddress : $strGyroAddress")
            val editor = settings!!.edit()
            editor.putString(Constants.PREF_GYRO_DEVICE, strGyroAddress)
            editor.apply()

            Toast.makeText(this, "GyroAddress : $strGyroAddress", Toast.LENGTH_SHORT).show()
        }

        binding.btReset.setOnClickListener {
            val strGasAddress = Constants.default_gas_address

            Log.e("eleutheria", "strGasAddress : $strGasAddress")
            val editor = settings!!.edit()
            editor.putString(Constants.PREF_GAS_DEVICE, strGasAddress)
            editor.apply()

//            val strGyroAddress = Constants.default_gyro_address

//            Log.e("eleutheria", "strGyroAddress : $strGyroAddress")
//            val editorFront = settings!!.edit()
//            editorFront.putString(Constants.PREF_GYRO_DEVICE, strGyroAddress)
//            editorFront.apply()

            if (strGasAddress != null) {
                val arGasData = strGasAddress.split(":")

                if(arGasData.size > 5) {
                    binding.etGas1.setText(arGasData[0])
                    binding.etGas2.setText(arGasData[1])
                    binding.etGas3.setText(arGasData[2])
                    binding.etGas4.setText(arGasData[3])
                    binding.etGas5.setText(arGasData[4])
                    binding.etGas6.setText(arGasData[5])
                }
            }

//            if (strGyroAddress != null) {
//                val arGyroData = strGyroAddress.split(":")
//
//                if(arGyroData.size > 5) {
//                    binding.etGyro1.setText(arGyroData[0])
//                    binding.etGyro2.setText(arGyroData[1])
//                    binding.etGyro3.setText(arGyroData[2])
//                    binding.etGyro4.setText(arGyroData[3])
//                    binding.etGyro5.setText(arGyroData[4])
//                    binding.etGyro6.setText(arGyroData[5])
//                }
//            }
        }
    }
}