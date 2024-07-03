package com.crc.ar.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_DATA
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.system.Os.close
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crc.ar.base.CommonUtils
import com.crc.ar.base.Constants
import com.crc.ar.emergency.EmergencyActivity
import org.json.JSONObject
import java.util.*

class BluetoothLeService : Service() {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val mGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                gatt.requestMtu(517)
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt!!.discoverServices())

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
//                disconnectGattServer()
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status)
            }
            setMCNotification(true)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic,
                                          status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }

        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)

            Log.e("eleutheria", "onMtuChanged mtu : $mtu")
            if(status == BluetoothGatt.GATT_SUCCESS) {
                gatt!!.discoverServices()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnectGattServer() {
        Log.d("eleutheria", "Closing Gatt connection")
        // reset the connection flag
        mConnectionState = STATE_DISCONNECTED

        // disconnect and close the gatt
        if (mBluetoothGatt != null) {
            mBluetoothGatt!!.disconnect()
            mBluetoothGatt!!.close()
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String,
                                characteristic: BluetoothGattCharacteristic
    ) {
        val intent = Intent(action)
//        Log.e("eleutheria", "broadcastUpdate")

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT == characteristic.uuid) {
            val flag = characteristic.properties
            var format = -1
            if (flag and 0x01 != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
                Log.d(TAG, "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8
                Log.d(TAG, "Heart rate format UINT8.")
            }
            val heartRate = characteristic.getIntValue(format, 1)!!
            Log.d(TAG, String.format("Received heart rate: %d", heartRate))
            intent.putExtra(EXTRA_DATA, heartRate.toString())
        } else {
            // For all other profiles, writes the data formatted in HEX.
            val data = characteristic.value

            if (data != null && data.size > 0) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data)
                    stringBuilder.append(String.format("%02X ", byteChar))
                intent.putExtra(EXTRA_DATA, String(data) + "\n" + stringBuilder.toString())

                sendMessageToActivity(String(data))
            }
        }
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        internal val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close()
        return super.onUnbind(intent)
    }

    private val mBinder = LocalBinder()

    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.

        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.")
                Log.e("eleutheria", "Unable to initialize BluetoothManager.")
                return false
            }
        }

        mBluetoothAdapter = mBluetoothManager!!.adapter
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            Log.e("eleutheria", "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            Log.w("eleutheria", "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress
            && mBluetoothGatt != null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            Log.w("eleutheria", "BluetoothAdapter not initialized or unspecified address.")
            if (mBluetoothGatt!!.connect()) {
                mConnectionState = STATE_CONNECTING
                return true
            } else {
                return false
            }
        }

        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @SuppressLint("MissingPermission")
    fun close() {
        if (mBluetoothGatt == null) {
            return
        }
        mBluetoothGatt!!.close()
        mBluetoothGatt = null
    }

    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.

     * @param characteristic The characteristic to read from.
     */
    @SuppressLint("MissingPermission")
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        Log.e("eleutheria", "characteristic: BluetoothGattCharacteristic")
        mBluetoothGatt!!.readCharacteristic(characteristic)
    }

    /**
     * Enables or disables notification on a give characteristic.

     * @param characteristic Characteristic to act on.
     * *
     * @param enabled If true, enable notification.  False otherwise.
     */
    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic,
                                      enabled: Boolean) {

        Log.e("eleutheria", "characteristic : ${characteristic.uuid}")
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
        Log.e("eleutheria", "setCharacteristicNotification characteristic.uuid : ${characteristic.uuid}")
        if (UUID_GAS_STATE == characteristic.uuid) {
//            Log.e("eleutheria", "strart")
//            SystemClock.sleep(5000)
//            Log.e("eleutheria", "end")
            val descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            val mDevice: BluetoothDevice? = mBluetoothGatt!!.device
            val mServices : List<BluetoothGattService> = mBluetoothGatt!!.services

            val characteristic = descriptor.characteristic

            if(characteristic != null) {
                Log.e("eleutheria", "characteristic != null")
            }

            val result : Boolean = mBluetoothGatt!!.writeDescriptor(descriptor)
            Log.e("eleutheria", "result : $result, mDevice : $mDevice, mServices : $mServices")
        }
    }

    fun setMCNotification(enabled: Boolean) {

        val uuidMCCharateristic = SampleGattAttributes.CHARACTERISTIC_GAS_STATE
        val uuidMCService = SampleGattAttributes.SERVICE_GAS_STATE

//        if(Constants.curState == Constants.STATE_CONNECT_GAS) {
//            Log.e("eleutheria", "STATE_CONNECT_GAS")
//            uuidMCCharateristic = SampleGattAttributes.CHARACTERISTIC_GAS_STATE
//            uuidMCService = SampleGattAttributes.SERVICE_GAS_STATE
//        } else if(Constants.curState == Constants.STATE_CONNECT_GYRO) {
//            Log.e("eleutheria", "STATE_CONNECT_GYRO")
//            uuidMCCharateristic = SampleGattAttributes.CHARACTERISTIC_GYRO_STATE
//            uuidMCService = SampleGattAttributes.SERVICE_GYRO_STATE
//        }


        var mBluetoothLeService: BluetoothGattService? = null
        var mBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null

        for(service: BluetoothGattService in mBluetoothGatt!!.getServices()) {
            if((service.uuid==null)) continue
            if(uuidMCService.equals(service.uuid.toString(), true)) {
                mBluetoothLeService = service
            }
        }
        if(mBluetoothLeService != null) {
            mBluetoothGattCharacteristic = mBluetoothLeService.getCharacteristic(UUID.fromString(uuidMCCharateristic))
        } else {
            Log.e("eleuthria", "mBluetoothLeService is null")
        }

        if(mBluetoothGattCharacteristic != null) {
            Log.e("eleuthria", "mBluetoothGattCharacteristic != null")
            setCharacteristicNotification(mBluetoothGattCharacteristic, enabled)
        }
    }

    private fun sendMessageToActivity(msg: String) {
        var strActionName = "NoAction"

        Log.e("eleuthria", "msg : $msg")

        val dustJsonObject = JSONObject()
        val gasJsonObject = JSONObject()
        var strSendData = ""


        if(msg.contains(Constants.RECIEVE_DATA_PREFIX_GYRO)) {

            val strReceiveData = msg.replace("!", "")
            val arGyroValue = strReceiveData.split(":")
            if(arGyroValue.size > 1) {
                Log.e("eleuthria", "arGyroValue[1] : ${arGyroValue[1]}")
                if(arGyroValue[1].equals("1")) {
                    if(!Constants.bEmergencyState) {
                        Constants.bEmergencyState = true
                        Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_EMERGENCY

                        val intent = Intent(this, EmergencyActivity::class.java).apply {
                            // Optional: add any extra data here
                            putExtra("EXTRA_DATA", strSendData)

                            // Add this flag since you're starting a new activity from the Service context
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    } else {
                        strSendData = msg
                    }
                }
            }

        } else if(msg.contains(Constants.RECIEVE_DATA_PREFIX_GAS)) {

            val strReceiveData = msg.replace("!", "")
            val arGasValue = strReceiveData.split(":")
            if(arGasValue.size > 1) {
                Log.e("eleuthria", "arGasValue[1] : ${arGasValue[1]}")
                if(arGasValue[1].equals(Constants.RECIEVE_DATA_PREFIX_CH4) || arGasValue[1].equals(Constants.RECIEVE_DATA_PREFIX_CO)
                    || arGasValue[1].equals(Constants.RECIEVE_DATA_PREFIX_CO2) || arGasValue[1].equals(Constants.RECIEVE_DATA_PREFIX_NH3)
                    || arGasValue[1].equals(Constants.RECIEVE_DATA_PREFIX_NO2)) {

                    if(!Constants.bEmergencyState) {
                        Constants.bEmergencyState = true
                        Constants.nCurFunctionIndex = Constants.MAIN_FUNCTION_INDEX_EMERGENCY

                        val intent = Intent(this, EmergencyActivity::class.java).apply {
                            // Optional: add any extra data here
                            putExtra("EXTRA_DATA", strSendData)

                            // Add this flag since you're starting a new activity from the Service context
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        startActivity(intent)
                    } else {
                        strSendData = msg
                    }

                }
//                strSendData = arGasValue[1]
//                val intent = Intent(this, EmergencyActivity::class.java).apply {
//                    // Optional: add any extra data here
//                    putExtra("EXTRA_DATA", strSendData)
//
////                    // Add this flag since you're starting a new activity from the Service context
////                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }
//
//                startActivity(intent)
            }

        } else {
            strSendData = msg
        }

        when(Constants.nCurFunctionIndex) {
            Constants.MAIN_FUNCTION_INDEX_FINEDUST -> {
                strActionName = Constants.MESSAGE_SEND_FINEDUST
            }
            Constants.MAIN_FUNCTION_INDEX_GAS -> {
                strActionName = Constants.MESSAGE_SEND_GAS
            }
            Constants.MAIN_FUNCTION_INDEX_EMERGENCY -> {
                strActionName = Constants.MESSAGE_SEND_EMERGENCY
            }
        }

        Log.e("eleuthria", "strSendData : $strSendData")
        val intent = Intent(strActionName)
        // You can also include some extra data.
        intent.putExtra("value", strSendData)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.

     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        get() {
            if (mBluetoothGatt == null) return null

            return mBluetoothGatt!!.services
        }



    companion object {
        private val TAG = BluetoothLeService::class.java.simpleName

        private val STATE_DISCONNECTED = 0
        private val STATE_CONNECTING = 1
        private val STATE_CONNECTED = 2

        val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"


        val UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)
        val UUID_GAS_STATE = UUID.fromString(SampleGattAttributes.CHARACTERISTIC_GAS_STATE)
//        val UUID_GYRO_STATE = UUID.fromString(SampleGattAttributes.CHARACTERISTIC_GYRO_STATE)
    }
}