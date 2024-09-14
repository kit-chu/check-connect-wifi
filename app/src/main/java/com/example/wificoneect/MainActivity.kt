package com.example.wificoneect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.wifi.ScanResult
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var wifiList: ListView
    private lateinit var moduleWifiConnect: ModuleWifiConnect
    private lateinit var btnScan: Button
    private lateinit var wifiConnectedSSID: TextView
    private lateinit var wifiConnectedBSSIS: TextView
    private val REQUEST_CODE_PERMISSION = 1001

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            wifiList = findViewById<ListView>(R.id.wifiList)
            wifiConnectedSSID = findViewById(R.id.wifiConnectedSSID)
            wifiConnectedBSSIS = findViewById(R.id.wifiConnectedBSSIS)
            btnScan = findViewById<Button>(R.id.scanBtn)
            val btnConnectHidden = findViewById<Button>(R.id.hiddenWifi)


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSION)
            } else {
            btnScan.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initializeModuleWifiConnect()
                } else {
                    Toast.makeText(this, "Location permission is required to scan Wi-Fi", Toast.LENGTH_LONG).show()
                }
            }
            }

            btnConnectHidden.setOnClickListener {
                // Implement logic to connect to hidden Wi-Fi
                // showConnectToHiddenWifiDialog()
            }
        }

        private fun initializeModuleWifiConnect() {
            moduleWifiConnect = ModuleWifiConnect(
              this,
              onScanResultsAvailable = { scanResults ->
                  if (scanResults != null){
                      updateWifiList(scanResults = scanResults)
                  }


              },
              onConnectedWifiAvailable = { connectedWifi ->
                  if (connectedWifi != null) {
                      wifiConnectedSSID.text = "SSID: ${connectedWifi.ssid}"
                      wifiConnectedBSSIS.text = "BSSID: ${connectedWifi.bssid}"
                  } else {
                      wifiConnectedSSID.text = "Not connected to any Wi-Fi"
                  }
              }
          )

            btnScan.setOnClickListener {
                moduleWifiConnect.getDeviceWiFi()
                moduleWifiConnect.checkConnectedWifi()
            }

        }

        private fun updateWifiList(scanResults: List<ScanResult>) {
            val wifiName = scanResults.map {result->
                "SSID: ${result.SSID}, BSSID: ${result.BSSID}"
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wifiName)
            wifiList.adapter = adapter

        }



        override fun onDestroy() {
            super.onDestroy()
            moduleWifiConnect.unregisterReceiver()
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeModuleWifiConnect()
            } else {
                Toast.makeText(this, "Permission denied. Cannot scan Wi-Fi.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    }


