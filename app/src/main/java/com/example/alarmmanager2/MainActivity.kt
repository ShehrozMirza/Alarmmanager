package com.example.alarmmanager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ReminderReceiver.startAlarm(this)
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.extras != null) {
                Toast.makeText(
                    context,
                    "Received Broadcast " + intent.extras?.get("message"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver
        val action = "com.send_data_alarm_manager"
        registerReceiver(receiver, IntentFilter(action))
    }

    override fun onPause() {
        super.onPause()
        // Unregister the receiver to save unnecessary system overhead
        // Paused activities cannot receive broadcasts anyway
        unregisterReceiver(receiver)
    }

    //    private fun addAutoStartup() {
//        try {
//            val intent = Intent()
//            val manufacturer = Build.MANUFACTURER
//            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
//                intent.component = ComponentName(
//                    "com.miui.securitycenter",
//                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
//                )
//            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
//                intent.component = ComponentName(
//                    "com.coloros.safecenter",
//                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
//                )
//            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
//                intent.component = ComponentName(
//                    "com.vivo.permissionmanager",
//                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
//                )
//            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
//                intent.component = ComponentName(
//                    "com.letv.android.letvsafe",
//                    "com.letv.android.letvsafe.AutobootManageActivity"
//                )
//            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
//                intent.component = ComponentName(
//                    "com.huawei.systemmanager",
//                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
//                )
//            }
//            val list =
//                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//            if (list.size > 0) {
//                startActivity(intent)
//            }
//        } catch (e: Exception) {
//            Log.e("exc", e.toString())
//        }
//    }
}