package com.example.sample_content_provider



import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btn)
        button.setOnClickListener {
            onClickAddDetails()
        }

        contentResolver.registerContentObserver(
            DeviceContract.DEVICE_CONTENT_URI,
            false,
            contentObserver
        )
    }

    private val contentObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.myLooper()!!)) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                onClickShowDetails();
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }



    fun onClickAddDetails() {


        var model = DeviceConnectedModel(
            index = 1,
            deviceId = LocalDateTime.now().second.toString(),
            name = "Duc",
            btAddress = "21122",
            supportType = "Uw",
            doNotShowAgain = Random.nextInt(0, 1),
            operationStatus = Random.nextInt(0, 1),
            isWireless = Random.nextInt(0, 1),
            isWirelessReady = Random.nextInt(0, 1)
        )
        // class to add values in the database
        val values = ContentValues()


        values.put(DeviceContract.DeviceEntry.COLUMN_DEVICE_ID, model.deviceId)
        values.put(DeviceContract.DeviceEntry.COLUMN_NAME, model.name)
        values.put(DeviceContract.DeviceEntry.COLUMN_BT_ADDRESS, model.btAddress)
        values.put(DeviceContract.DeviceEntry.COLUMN_SUPPORT_TYPE, model.supportType)
        values.put(DeviceContract.DeviceEntry.COLUMN_DO_NOT_SHOW_AGAIN, model.doNotShowAgain)
        values.put(DeviceContract.DeviceEntry.COLUMN_OPERATION_STATUS, model.operationStatus)
        values.put(DeviceContract.DeviceEntry.COLUMN_IS_WIRELESS, model.isWireless)
        values.put(DeviceContract.DeviceEntry.COLUMN_IS_WIRELESS_READY, model.isWirelessReady)

        // inserting into database through content URI
        contentResolver.insert(DeviceContract.DEVICE_CONTENT_URI, values)

        // displaying a toast message
        Toast.makeText(baseContext, "New Record Inserted", Toast.LENGTH_LONG).show()
    }

    fun onClickShowDetails() {

        val cursor = contentResolver.query(
            DeviceContract.DEVICE_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor!!.moveToFirst()) {
            val strBuild = StringBuilder()
            while (!cursor.isAfterLast) {
                Log.d(
                    "Edward",
                    cursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_DEVICE_ID).toString()
                )
                cursor.moveToNext()
            }

        }
    }
}
