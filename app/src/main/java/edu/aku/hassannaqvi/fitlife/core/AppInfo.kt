package edu.aku.hassannaqvi.fitlife.core

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
 * @author Mustufa
 * @update Ali Azaz Alam dt. 12.16.20
 * */
class AppInfo {
    var versionName: String = ""
    var installedOn: Long = 0
    var versionCode: Long = 0
    var tagName: String? = null
    var deviceID: String = ""
    var appVersion: String = ""
    val dtToday: String
        get() = SimpleDateFormat("dd-MM-yy HH:mm", Locale.ENGLISH).format(Date().time)
    val isTestingApp: Boolean
        // get() = versionName.split("\\.".toRegex())[0].toInt() > 0
        get() = versionName.split(".").first().toInt() > 0


    lateinit var dbHelper: DatabaseHelper

    constructor(context: Context) {
        try {
            installedOn = context.packageManager.getPackageInfo(
                context.applicationContext.packageName,
                0
            ).lastUpdateTime
            versionCode = context.packageManager.getPackageInfo(
                context.applicationContext.packageName,
                0
            ).longVersionCode
            versionName = context.packageManager.getPackageInfo(
                context.applicationContext.packageName,
                0
            ).versionName
            deviceID =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            appVersion = "$versionName.$versionCode"
            tagName = getTagName(context)
            synchronized(this) {
                dbHelper = DatabaseHelper(context)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    constructor(versionName: String, installedOn: Long, versionCode: Long) {
        this.versionName = versionName
        this.installedOn = installedOn
        this.versionCode = versionCode
    }

    private fun getTagName(mContext: Context): String? {
        val sharedPref = mContext.getSharedPreferences("tagName", Context.MODE_PRIVATE)
        return sharedPref.getString("tagName", null)
    }

    fun updateTagName(mContext: Context) {
        tagName = getTagName(mContext)
    }

    fun getInfo(): AppInfo {
        return AppInfo(versionName, installedOn, versionCode)
    }

    fun getAppInfo(): String {
        return """Ver. $versionName.$versionCode ( Last Updated: ${
            SimpleDateFormat(
                "dd MMM. yyyy",
                Locale.ENGLISH
            ).format(Date(getInfo().installedOn))
        } )"""
    }
}