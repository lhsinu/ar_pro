package com.crc.ar.base

class Constants {
    companion object {

        var strDeviceName = "Not Connected"
        var strDeviceAddress = "Not Connected"

        var bGasState                                   = false
        var bGyroState                                  = false
        var bEmergencyState                             = false

        var strEmergencyNumber: String = "01000000000"

        val SHARED_PREF_SEUPDATA = "setupdData"

        val PREF_EMERGENCY_CALL_NUMBER : String                          = "prefEmergencyCallNumber"

        var nCurFunctionIndex                           = 0
//        val SELECT_FUNCTION_INDEX                       = "Select_Index"

        val MAIN_FUNCTION_INDEX_FINEDUST                = 0
        val MAIN_FUNCTION_INDEX_GAS                     = 1
//        val MAIN_FUNCTION_INDEX_GYRO                    = 2
        val MAIN_FUNCTION_INDEX_EMERGENCY               = 2
        val MAIN_FUNCTION_INDEX_SETTING                 = 3

        val MESSAGE_SEND_FINEDUST                       = "FineDustMessage"
        val MESSAGE_SEND_GAS                            = "GasMessage"
        val MESSAGE_SEND_GYRO                           = "GyroMessage"
        val MESSAGE_SEND_EMERGENCY                      = "EmergencyMessage"

        val RECIEVE_DATA_PREFIX_FINEDUST25                      = "P2"
        val RECIEVE_DATA_PREFIX_FINEDUST10                      = "P1"
        val RECIEVE_DATA_PREFIX_FINEDUST                        = "PM"
        val RECIEVE_DATA_PREFIX_NO2                             = "NO"
        val RECIEVE_DATA_PREFIX_CO                              = "CO"
        val RECIEVE_DATA_PREFIX_NH3                             = "NH"
        val RECIEVE_DATA_PREFIX_CO2                             = "C2"
        val RECIEVE_DATA_PREFIX_CH4                             = "C4"
        val RECIEVE_DATA_PREFIX_PEAPLE                          = "PE"
        val RECIEVE_DATA_PREFIX_TEMPERATURE                     = "TE"
        val RECIEVE_DATA_PREFIX_GYRO                            = "G"
        val RECIEVE_DATA_PREFIX_GAS                             = "T"

        val MAXIMUM_VALUE_FINEDUST : Int                        = 1000
        val MAXIMUM_VALUE_NO2 : Float                           = 0.2F
        val MAXIMUM_VALUE_CO : Int                              = 25
        val MAXIMUM_VALUE_NH3 : Int                             = 25
        val MAXIMUM_VALUE_C4H10 : Int                           = 800
        val MAXIMUM_VALUE_CH4 : Int                             = 1000
        val MAXIMUM_VALUE_TEMPERATURE : Int                     = 1000

        val TEMPERATURE_COLD : Int                  = 16
        val TEMPERATURE_GOOD : Int                  = 28

        val SHARED_PREF_DEVICEDATA : String                         = "deviceData"
        val PREF_GAS_DEVICE : String                                = "prefGasDevice"
        val PREF_GYRO_DEVICE : String                               = "prefGyroDevice"

        var curState : Int                                          = 0
        val STATE_CONNECT_GAS : Int                                 = 1
        val STATE_CONNECT_GYRO : Int                                = 2

        var default_gas_address                                 = "D4:D4:DA:83:0E:A6"
//        var default_gas_address                                 = "50:02:91:95:8A:D2"
//        var default_gyro_address                                = "30:C6:F7:04:43:E6"

        var MODULE_ADDRESS_GAS                                  = default_gas_address
        val MODULE_SERVICE_UUID_GAS                             = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
        val MODULE_CHARACTERISTIC_UUID_GAS                      = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"

//        var MODULE_ADDRESS_GYRO                                 = default_gyro_address
//        val MODULE_SERVICE_UUID_GYRO                            = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
//        val MODULE_CHARACTERISTIC_UUID_GYRO                     = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"

    }
}