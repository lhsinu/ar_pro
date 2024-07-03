package com.crc.ar.temperature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.MainActivity
import com.crc.ar.R
import com.crc.ar.base.Constants

class TemperatureActivity : AppCompatActivity(), View.OnClickListener  {

    lateinit var ivTemperatureBg: ImageView
    lateinit var tvTemperatureText: TextView
    lateinit var tvHumidityText: TextView
    var nTemperature = 2
    var nHumidity = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature)

        supportActionBar!!.hide()

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_temperature_title)

        val btToolbarBack : Button = findViewById(R.id.bt_toolbar_back)
        btToolbarBack.setOnClickListener(this)

        ivTemperatureBg = findViewById(R.id.iv_temperature_bg)
        tvTemperatureText = findViewById(R.id.tv_temperature_text)
        tvHumidityText = findViewById(R.id.tv_current_humidity_value)

        displayTemperature(nTemperature, nHumidity)
    }

    private fun displayTemperature(nTemperature: Int, nHumidity : Int) {
        if(nTemperature <= Constants.TEMPERATURE_COLD) {
            var strCurTemperature = nTemperature.toString() +  getString(R.string.str_main_default_temperature)
            tvTemperatureText.text = strCurTemperature
            ivTemperatureBg.setImageResource(R.drawable.temperature_cold)
        } else if(nTemperature <= Constants.TEMPERATURE_GOOD) {
            var strCurTemperature = nTemperature.toString() +  getString(R.string.str_main_default_temperature)
            tvTemperatureText.text = strCurTemperature
            ivTemperatureBg.setImageResource(R.drawable.temperature_good)
        } else {
            var strCurTemperature = nTemperature.toString() +  getString(R.string.str_main_default_temperature)
            tvTemperatureText.text = strCurTemperature
            ivTemperatureBg.setImageResource(R.drawable.temperature_hot)
        }

        tvHumidityText.text = nHumidity.toString()
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.bt_toolbar_back -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {

        val activityIntent : Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        startActivity(activityIntent)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter(Constants.MESSAGE_SEND_FINEDUST))
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.hasExtra("value")) {
                val message = intent.getStringExtra("value")

                if (message != null) {
                    displayTemperature(message.toInt(), message.toInt())
                }

            }
        }

    }
}