package com.crc.ar.finedust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.MainActivity
import com.crc.ar.R
import com.crc.ar.base.Constants
import com.crc.ar.databinding.ActivityFineDustBinding

class FineDustActivity: AppCompatActivity(), View.OnClickListener  {
    private lateinit var binding: ActivityFineDustBinding


    var nFineDust25 = 43
    var nFineDust10 = 54
    var nHuman = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFineDustBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_finedust_title)

        val btToolbarBack : Button = findViewById(R.id.bt_toolbar_back)
        btToolbarBack.setOnClickListener(this)

//        tvPM1Text.text = nFineDust1.toString()
        binding.tvFinedust2Text.text = nFineDust25.toString()
        binding.tvFinedust3Text.text = nFineDust10.toString()
        binding.tvHumanText.text = nHuman.toString()
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                Constants.MESSAGE_SEND_FINEDUST
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
                                    binding.tvFinedust2Text.text = value
                                }
                                Constants.RECIEVE_DATA_PREFIX_FINEDUST10 -> {
                                    binding.tvFinedust3Text.text = value
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}