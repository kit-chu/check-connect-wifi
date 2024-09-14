package com.example.wificoneect

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.net.wifi.WifiManager
import android.widget.Toast
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo


class ModuleWifiConnect(private val context: Context,
                        private val onScanResultsAvailable: (List<ScanResult>) -> Unit,
                        private val onConnectedWifiAvailable: (WifiInfo?) -> Unit,
) {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }


    init {
        val intentFilter = IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        context.registerReceiver(wifiScanReceiver, intentFilter)

        // Start scanning for Wi-Fi networks
        val success = wifiManager.startScan()
        if(wifiManager.isWifiEnabled){
            if (!success) {
                scanFailure()
            }else{
                checkConnectedWifi()
            }
        }
        else {
            Toast.makeText(context, "Wi-Fi is disabled.", Toast.LENGTH_SHORT).show()
        }
    }



        public fun  getDeviceWiFi(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver,intentFilter)
        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
    }
    private fun scanSuccess() {
        try {
            val results = wifiManager.scanResults
            onScanResultsAvailable(results)
            Toast.makeText(context, "Scan successful! Found ${results.size} networks.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun scanFailure() {
        try {
            val results = wifiManager.scanResults
            onScanResultsAvailable(results)
            Toast.makeText(context, "Scan failed. Using old results.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    public  fun unregisterReceiver() {
        context.unregisterReceiver(wifiScanReceiver)
    }

    fun checkConnectedWifi() {
        try {
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null && wifiInfo.supplicantState.name == "COMPLETED") {
                // ส่งข้อมูล Wi-Fi ที่เชื่อมต่อไปยัง callback
                onConnectedWifiAvailable(wifiInfo)
            } else {
                // ไม่มี Wi-Fi ที่เชื่อมต่อ
                onConnectedWifiAvailable(null)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error retrieving connected Wi-Fi info: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
