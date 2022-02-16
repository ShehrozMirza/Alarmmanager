package com.example.alarmmanager2

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker

class MainActivity : AppCompatActivity() {
    lateinit var previewSelectedTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // instance of the UI elements
        val buttonPickTime: Button = findViewById<Button>(R.id.pick_time_button)
        previewSelectedTimeTextView = findViewById<TextView>(R.id.preview_picked_time_textView)

        // handle the pick time button to
        // open the TimePickerDialog
        buttonPickTime.setOnClickListener {
            val timePicker: TimePickerDialog = TimePickerDialog(
                // pass the Context
                this,
                // listener to perform task
                // when time is picked
                timePickerDialogListener,
                // default hour when the time picker
                // dialog is opened
                12,
                // default minute when the time picker
                // dialog is opened
                0,
                // 24 hours time picker is
                // false (varies according to the region)
                false
            )

            // then after building the timepicker
            // dialog show the dialog to user
            timePicker.show()
        }


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

    // listener which is triggered when the
    // time is picked from the time picker dialog
    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute -> // logic to properly handle
            // the picked timings by user
            val formattedTime: String = when {
                hourOfDay == 0 -> {
                    if (minute < 10) {
                        "${hourOfDay + 12}:0${minute} am"
                    } else {
                        "${hourOfDay + 12}:${minute} am"
                    }
                }
                hourOfDay > 12 -> {
                    if (minute < 10) {
                        "${hourOfDay - 12}:0${minute} pm"
                    } else {
                        "${hourOfDay - 12}:${minute} pm"
                    }
                }
                hourOfDay == 12 -> {
                    if (minute < 10) {
                        "${hourOfDay}:0${minute} pm"
                    } else {
                        "${hourOfDay}:${minute} pm"
                    }
                }
                else -> {
                    if (minute < 10) {
                        "${hourOfDay}:${minute} am"
                    } else {
                        "${hourOfDay}:${minute} am"
                    }
                }
            }

            ReminderReceiver.startAlarm(this, hourOfDay, minute)
            previewSelectedTimeTextView.text = formattedTime
        }
}