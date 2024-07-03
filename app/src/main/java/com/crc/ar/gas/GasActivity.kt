package com.crc.ar.gas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.MainActivity
import com.crc.ar.R
import com.crc.ar.base.CommonUtils
import com.crc.ar.base.Constants
import com.crc.ar.databinding.ActivityGasBinding

class GasActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGasBinding

    private lateinit var commonUtils : CommonUtils
    private lateinit var mContext : Context


    private var fNo2 : Float = 0.1f
    private var nCo : Int = 477
    private var nNh3 : Int = 134
    private var nC4h10 : Int = 13
    private var nCh4 : Int = 6
    private var nHuman : Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        commonUtils = CommonUtils()
        mContext = this

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_gas_title)

        val btToolbarBack : Button = findViewById(R.id.bt_toolbar_back)
        btToolbarBack.setOnClickListener(this)

        binding.tvNo2Text.text = fNo2.toString()
        binding.tvCoText.text = nCo.toString()
        binding.tvNh3Text.text = nNh3.toString()
        binding.tvCo2Text.text = nC4h10.toString()
        binding.tvCh4Text.text = nCh4.toString()
        binding.tvHumanText.text = nHuman.toString()
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                Constants.MESSAGE_SEND_GAS
            )
        )
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.bt_toolbar_back -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        val activityIntent : Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        startActivity(activityIntent)
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.hasExtra("value")) {
                val message = intent.getStringExtra("value")
                Log.d("eleutheria", String.format("Gas Value : $message"))

                // p2: value / p1: value / co: value / c3: value / ch: value / c4: value / nh: value / no: value!
//                message = "p2: value / p1: value / co: value / c3: value / ch: value / c4: value / nh: value / no: value!"
//                message = "pe:3!"

                if(message != null) {
                    val strReceiveData = message.replace("!", "")
                    val keyValuePairList = strReceiveData.split("/")

                    for (pair in keyValuePairList) {
                        val keyValue = pair.split(":")
                        if (keyValue.size == 2) {
                            val key = keyValue[0].trim()
                            val value = keyValue[1].trim()

                            when(key) {
                                Constants.RECIEVE_DATA_PREFIX_NO2 -> {
                                    binding.tvNo2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CO -> {
                                    binding.tvCoText.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_NH3 -> {
                                    binding.tvNh3Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CO2 -> {
                                    binding.tvCo2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_CH4 -> {
                                    binding.tvCh4Text.text = value
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}