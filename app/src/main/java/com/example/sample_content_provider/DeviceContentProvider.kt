package com.example.sample_content_provider
import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns

class DeviceContentProvider : ContentProvider() {

    private lateinit var dbHelper: DeviceDbHelper

    override fun onCreate(): Boolean {
        val context: Context = context!!
        dbHelper = DeviceDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = when (sUriMatcher.match(uri)) {
            DEVICE -> db.query(
                DeviceContract.DeviceEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null // Modify this method if needed
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val db = dbHelper.writableDatabase
        val insertedRowId = when (sUriMatcher.match(uri)) {
            DEVICE -> db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (insertedRowId != -1L) {
            context!!.contentResolver?.notifyChange(uri, null)
            return ContentUris.withAppendedId(uri, insertedRowId)
        }
        throw RuntimeException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val deletedRowCount = when (sUriMatcher.match(uri)) {
            DEVICE -> db.delete(DeviceContract.DeviceEntry.TABLE_NAME, selection, selectionArgs)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (deletedRowCount != 0) {
            context!!.contentResolver?.notifyChange(uri, null)
        }
        return deletedRowCount
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val updatedRowCount = when (sUriMatcher.match(uri)) {
            DEVICE -> db.update(
                DeviceContract.DeviceEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (updatedRowCount != 0) {
            context!!.contentResolver?.notifyChange(uri, null)
        }
        return updatedRowCount
    }

    companion object {
        private const val DEVICE = 1
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(DeviceContract.AUTHORITY, "device", DEVICE)
        }
    }

    class DeviceDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            val createTableQuery =
                ("CREATE TABLE ${DeviceContract.DeviceEntry.TABLE_NAME}  "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                        + "${DeviceContract.DeviceEntry.COLUMN_DEVICE_ID} TEXT,"
                        + "${DeviceContract.DeviceEntry.COLUMN_NAME} TEXT,"
                        + "${DeviceContract.DeviceEntry.COLUMN_BT_ADDRESS} TEXT,"
                        + "${DeviceContract.DeviceEntry.COLUMN_SUPPORT_TYPE} TEXT,"
                        + "${DeviceContract.DeviceEntry.COLUMN_DO_NOT_SHOW_AGAIN} INTEGER,"
                        + "${DeviceContract.DeviceEntry.COLUMN_OPERATION_STATUS} INTEGER,"
                        + "${DeviceContract.DeviceEntry.COLUMN_IS_WIRELESS} INTEGER,"
                        + "${DeviceContract.DeviceEntry.COLUMN_IS_WIRELESS_READY} INTEGER,"
                        + "${DeviceContract.DeviceEntry.COLUMN_ADDITIONAL_PROPERTY_1} TEXT,"
                        + "${DeviceContract.DeviceEntry.COLUMN_ADDITIONAL_PROPERTY_2} INTEGER);"
                        )
            db.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Implement database schema upgrade if needed
        }

        companion object {
            private const val DATABASE_NAME = "device.db"
            private const val DATABASE_VERSION = 1
        }
    }
}

object DeviceContract {
    const val AUTHORITY = "com.example.sample_content_provider"

    val DEVICE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/device")

    object DeviceEntry : BaseColumns {
        const val TABLE_NAME = "device"
        const val COLUMN_DEVICE_ID = "deviceId"
        const val COLUMN_NAME = "name"
        const val COLUMN_BT_ADDRESS = "btAddress"
        const val COLUMN_SUPPORT_TYPE = "supportType"
        const val COLUMN_DO_NOT_SHOW_AGAIN = "doNotShowAgain"
        const val COLUMN_OPERATION_STATUS = "operationStatus"
        const val COLUMN_IS_WIRELESS = "isWireless"
        const val COLUMN_IS_WIRELESS_READY = "isWirelessReady"
        const val COLUMN_ADDITIONAL_PROPERTY_1 = "additionalProperty1"
        const val COLUMN_ADDITIONAL_PROPERTY_2 = "additionalProperty2"
        // Add more column names as needed
    }
}
