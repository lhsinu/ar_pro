package com.crc.ar.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    fun printScreenInfo() {
        Log.d("eleutheria", Resources.getSystem().displayMetrics.density.toString())

        Log.d("eleutheria", Resources.getSystem().displayMetrics.densityDpi.toString())
        Log.d("eleutheria", Resources.getSystem().displayMetrics.scaledDensity.toString())
        Log.d("eleutheria", Resources.getSystem().displayMetrics.heightPixels.toString())
        Log.d("eleutheria", Resources.getSystem().displayMetrics.widthPixels.toString())
        Log.d("eleutheria", Resources.getSystem().displayMetrics.xdpi.toString())
        Log.d("eleutheria", Resources.getSystem().displayMetrics.ydpi.toString())
    }

    fun getCurrentDate() : List<String> {

        val sdf = SimpleDateFormat("yyyy/MM/dd/HH/mm/ss")
        val currentDate = sdf.format(Date())

        var curDate = currentDate.split("/")


        return curDate
    }

    fun sendSMS() {
        val strPhoneNumber = Constants.strEmergencyNumber

        val smsManager = SmsManager.getDefault()

        val message = "Gyro Action Message!! "
//        val message = "비상 상황입니다! 구조가 필요합니다!"
//        val message = "Warning! The concentration of GAS ‘" + strGasName + "’ is exceeded the permissible level at the site!"
        val strFirstString = strPhoneNumber.substring(0, 1)

        Log.e("eleutheria", "sendSMS message : $message")

        if(strFirstString.equals("0")) {
            Log.e("eleutheria", "strPhoneNumber : $strPhoneNumber")
            smsManager.sendTextMessage(strPhoneNumber, null, message, null, null)
        }
    }

    fun sendSMSGas(strGasName : String) {
        val strPhoneNumber = Constants.strEmergencyNumber

        val smsManager = SmsManager.getDefault()

//        val message = "Gyro Action Message!! "
//        val message = "비상 상황입니다! 구조가 필요합니다!"
        val message = "Warning! The concentration of GAS ‘" + strGasName + "’ is exceeded the permissible level at the site!"
        val strFirstString = strPhoneNumber.substring(0, 1)

        Log.e("eleutheria", "sendSMSGas message : $message")

        if(strFirstString.equals("0")) {
            Log.e("eleutheria", "strPhoneNumber : $strPhoneNumber")
            smsManager.sendTextMessage(strPhoneNumber, null, message, null, null)
        }
    }

    fun sendCall(context: Context) {
        val strPhoneNumber = Constants.strEmergencyNumber

        Log.e("eleutheria", "sendCall : $strPhoneNumber")

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$strPhoneNumber"))
        context.startActivity(intent)
    }
}