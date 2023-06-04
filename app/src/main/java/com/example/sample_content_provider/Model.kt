package com.example.sample_content_provider

data class DeviceConnectedModel(
    var index: Int = 0,
    var deviceId: String = "",
    var name: String = "",
    var btAddress: String = "",
    var supportType: String = "",
    var doNotShowAgain: Int = 0,
    var operationStatus: Int = 0,
    var isWireless: Int = 0,
    var isWirelessReady: Int = 0,
    // Add more properties as needed
)