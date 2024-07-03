package com.crc.ar.bluetooth

import com.crc.ar.base.Constants
import java.util.HashMap

object SampleGattAttributes {
    var attributes: HashMap<String, String> = HashMap()
    var HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG =              "00002902-0000-1000-8000-00805f9b34fb"

    // service
    var SERVICE_GAS_STATE                                        = Constants.MODULE_SERVICE_UUID_GAS
//    var SERVICE_GYRO_STATE                                         = Constants.MODULE_SERVICE_UUID_GYRO

    //characteristic
    var CHARACTERISTIC_GAS_STATE                                 = Constants.MODULE_CHARACTERISTIC_UUID_GAS
//    var CHARACTERISTIC_GYRO_STATE                                  = Constants.MODULE_CHARACTERISTIC_UUID_GYRO

    init {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service")
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service")
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement")
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String")


        // Using unknown GATT profile, must debug other end
        attributes.put("19B10000-E8F2-537E-4F6C-D104768A1214", "ioTank")
        attributes.put(SERVICE_GAS_STATE, "Gas State")
//        attributes.put(SERVICE_GYRO_STATE, "Gyro State")
    }


    fun lookup(uuid: String, defaultName: String): String {
        val name = attributes.get(uuid)
        return name ?: defaultName
    }
}