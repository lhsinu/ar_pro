package com.crc.ar.gyro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.MainActivity
import com.crc.ar.R
import com.crc.ar.base.Constants
import java.util.*

class GyroActivity : AppCompatActivity(), View.OnClickListener  {

    val handler: Handler = Handler1()
    private val mAlertTimer by lazy { Timer() }
    var isAlertOn = false

    lateinit var ivGyroReady : ImageView
    lateinit var ivGyroOnoff : ImageView
    lateinit var clGyroLayout: ConstraintLayout
    var bWarning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyro)

        supportActionBar!!.hide()

        val tvToolbarTitle : TextView = findViewById(R.id.tv_toolbar_title)
        tvToolbarTitle.text = getString(R.string.str_gyro_title)

        val btToolbarBack : Button = findViewById(R.id.bt_toolbar_back)
        btToolbarBack.setOnClickListener(this)

        ivGyroReady = findViewById(R.id.iv_gyro_ready)
        ivGyroReady.setOnClickListener(this)
        ivGyroOnoff = findViewById(R.id.iv_gyro_onoff)
        clGyroLayout = findViewById(R.id.cl_gyro_layout)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter(
                Constants.MESSAGE_SEND_GYRO
            )
        )
    }

    private fun alertByGyro() {
        val task = object : TimerTask() {
            override fun run() {

                runOnUiThread {
                    ivGyroReady.visibility = View.GONE
                    ivGyroOnoff.visibility = View.VISIBLE
                }

                val msg = handler.obtainMessage()
                handler.sendMessage(msg)
            }

        }
        mAlertTimer.schedule(task, 1000, 1000)
    }

    inner class Handler1 : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) = if(isAlertOn) {
            ivGyroOnoff.setImageResource(R.drawable.gyrosensing_emergency_image2)
            clGyroLayout.setBackgroundColor(resources.getColor(R.color.colorWhite))

            isAlertOn = false
        } else {
            ivGyroOnoff.setImageResource(R.drawable.gyrosensing_emergency_image1)
            clGyroLayout.setBackgroundColor(resources.getColor(R.color.colorActionBar))

            isAlertOn = true
        }
    }

    private fun sendSMS() {
        val strPhoneNumber = Constants.strEmergencyNumber

        val smsManager = SmsManager.getDefault()

//        val message = "Gyro Action Message!! "
        val message = "비상 상황입니다! 구조가 필요합니다!"
        val strFirstString = strPhoneNumber.substring(0, 1)

        if(strFirstString.equals("0")) {
            Log.e("eleutheria", "strPhoneNumber : $strPhoneNumber")
            smsManager.sendTextMessage(strPhoneNumber, null, message, null, null)
        }
    }

    private fun sendCall() {
        val strPhoneNumber = Constants.strEmergencyNumber

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$strPhoneNumber"))
        startActivity(intent)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.bt_toolbar_back -> {
                onBackPressed()
            }
            R.id.iv_gyro_ready -> {
                alertByGyro()
//                sendSMS()
//                sendCall()
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
                if(message.equals("1")) {
                    if(!bWarning) {
                        Log.e("eleutheria", "warning : $bWarning")
                        bWarning = true
                        alertByGyro()
                        sendSMS()
                        sendCall()
                    }
                }
            }
        }
    }
}