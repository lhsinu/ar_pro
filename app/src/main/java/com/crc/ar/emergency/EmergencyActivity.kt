package com.crc.ar.emergency

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.MainActivity
import com.crc.ar.R
import com.crc.ar.base.CommonUtils
import com.crc.ar.base.Constants
import com.crc.ar.databinding.ActivityEmergencyBinding
import java.util.Timer
import java.util.TimerTask

class EmergencyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEmergencyBinding

    val handler: Handler = HandlerEmergency()
    private val mAlertTimer by lazy { Timer() }
    var isAlertOn = false

    private lateinit var commonUtils : CommonUtils
    private lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_emergency_title_eng)

        val btToolbarBack : Button = findViewById(R.id.bt_toolbar_back)
        btToolbarBack.setOnClickListener(this)

        commonUtils = CommonUtils()
        mContext = this

        binding.ivGyroReady.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.bt_toolbar_back -> {
                onBackPressed()
            }
            R.id.iv_gyro_ready -> {
                alertByGyro()
//                commonUtils.sendSMS()
//                commonUtils.sendCall(mContext)
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        Constants.bEmergencyState                               = false
        Constants.nCurFunctionIndex                             = 0
        Constants.bGyroState                                    = false

        val activityIntent : Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        startActivity(activityIntent)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                Constants.MESSAGE_SEND_EMERGENCY
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        Constants.bEmergencyState                               = false
        Constants.nCurFunctionIndex                             = 0
        Constants.bGyroState                                    = false

    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.hasExtra("value")) {
                val message = intent.getStringExtra("value")

                if(message != null) {
                    val strReceiveData = message.replace("!", "")
                    val keyValuePairList = strReceiveData.split("/")

                    for (pair in keyValuePairList) {
                        val keyValue = pair.split(":")
                        if (keyValue.size == 2) {
                            val key = keyValue[0].trim()
                            val value = keyValue[1].trim()

                            when(key) {
                                Constants.RECIEVE_DATA_PREFIX_FINEDUST25 -> {
                                    binding.tvEmergencyPm2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_FINEDUST10 -> {
                                    binding.tvEmergencyPm1Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_NO2 -> {
                                    binding.tvEmergencyNo2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CO -> {
                                    binding.tvEmergencyCoText.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_NH3 -> {
                                    binding.tvEmergencyNh3Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CO2 -> {
                                    binding.tvEmergencyCo2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CH4 -> {
                                    binding.tvEmergencyCh4Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_PEAPLE -> {
                                    binding.tvEmergencyHumanText.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_GYRO -> {
                                    if(!Constants.bGyroState && value.equals("1")) {
                                        Constants.bGyroState = true
                                        alertByGyro()
                                        commonUtils.sendSMS()
                                        commonUtils.sendCall(mContext)
                                    }
                                }
                                Constants.RECIEVE_DATA_PREFIX_GAS -> {
                                    when(value) {
                                        Constants.RECIEVE_DATA_PREFIX_NO2 -> {
                                            binding.tvEmergencyNo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.colorRed)))
                                            binding.tvEmergencyCoText.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyNh3Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCh4Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                        }
                                        Constants.RECIEVE_DATA_PREFIX_CO -> {
                                            binding.tvEmergencyNo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCoText.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.colorRed)))
                                            binding.tvEmergencyNh3Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCh4Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                        }
                                        Constants.RECIEVE_DATA_PREFIX_NH3 -> {
                                            binding.tvEmergencyNo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCoText.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyNh3Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.colorRed)))
                                            binding.tvEmergencyCo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCh4Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                        }
                                        Constants.RECIEVE_DATA_PREFIX_CO2 -> {
                                            binding.tvEmergencyNo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCoText.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyNh3Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.colorRed)))
                                            binding.tvEmergencyCh4Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                        }
                                        Constants.RECIEVE_DATA_PREFIX_CH4 -> {
                                            binding.tvEmergencyNo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCoText.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyNh3Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCo2Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.black)))
                                            binding.tvEmergencyCh4Text.setTextColor(ColorStateList.valueOf(
                                                ContextCompat.getColor(applicationContext, R.color.colorRed)))
                                        }
                                    }
                                    if(!Constants.bGasState) {
                                        Constants.bGasState = true
                                        commonUtils.sendSMSGas(value)
                                        commonUtils.sendCall(mContext)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun alertByGyro() {
        val task = object : TimerTask() {
            override fun run() {

                runOnUiThread {
                    binding.ivGyroReady.visibility = View.GONE
                    binding.ivGyroOnoff.visibility = View.VISIBLE
                }

                val msg = handler.obtainMessage()
                handler.sendMessage(msg)
            }

        }
        mAlertTimer.schedule(task, 1000, 1000)
    }

    @SuppressLint("HandlerLeak")
    inner class HandlerEmergency : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) = if(isAlertOn) {
            binding.ivGyroOnoff.setImageResource(R.drawable.gyrosensing_emergency_image2)
            binding.clEmergencyGyro.setBackgroundColor(resources.getColor(R.color.colorWhite))

            isAlertOn = false
        } else {
            binding.ivGyroOnoff.setImageResource(R.drawable.gyrosensing_emergency_image1)
            binding.clEmergencyGyro.setBackgroundColor(resources.getColor(R.color.colorActionBar))

            isAlertOn = true
        }
    }
}