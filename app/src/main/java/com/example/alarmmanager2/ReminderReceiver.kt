package com.example.alarmmanager2

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import android.widget.RemoteViews
import android.media.MediaPlayer

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val REQUEST_TIMER1 = 1
        private const val PARAM_NAME = "name"

        private fun getIntent(context: Context, requestCode: Int): PendingIntent? {
            val intent = Intent(context, ReminderReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun startAlarm(context: Context, startMillis: Long? = null) {

            val pendingIntent = getIntent(context, REQUEST_TIMER1)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // trigger at 5:45 pm
            val alarmTime = LocalTime.of(18, 6)

            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val nowTime = now.toLocalTime()
            // if same time, schedule for next day as well
            // if today's time had passed, schedule for next day
            if (nowTime == alarmTime || nowTime.isAfter(alarmTime)) {
                now = now.plusDays(1)
            }
            now = now.withHour(alarmTime.hour)
                .withMinute(alarmTime.minute)

            // alarm use UTC/GMT time
            val utc = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime()
            val startMillis = utc.atZone(ZoneOffset.UTC)!!.toInstant()!!.toEpochMilli()

            Timber.d("Alarm will trigger in ${(startMillis - System.currentTimeMillis()) / 1000}s")

            //Different methods for setting alarm...

            //AlarmManagerCompat.setExact(alarm, AlarmManager.RTC_WAKEUP, startMillis, pendingIntent!!)
            //alarm.setExact(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent)
            // effort to save battery, allow deviation of 15 minutes
            // val windowMillis = 15L * 60L * 1_000L
            //alarm.setWindow(AlarmManager.RTC_WAKEUP, startMillis, windowMillis, pendingIntent)

            val localAlarmClockInfo = AlarmManager.AlarmClockInfo(startMillis, pendingIntent)
            alarm.setAlarmClock(localAlarmClockInfo, pendingIntent)

        }

        fun cancelAlarm(context: Context) {
            val pendingIntent = getIntent(context, REQUEST_TIMER1)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    //this method invokes when alarm triggered successfully
    // and set again if the case is successful or error.
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val name = intent?.getStringExtra(PARAM_NAME)

            Timber.d("onReceive, name=$name")
            showNotification(context!!)
            val intent: Intent =
                Intent("com.send_data_alarm_manager")
                    .putExtra("message", "Alarm Triggered")
            context.sendBroadcast(intent)

        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            //use try/catch to make sure next day alarm is set even if exception happens
            //start alarm for next day
            startAlarm(context!!)
        }
    }

    //set the alarm again if mobile is restarted...
    class BootReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("onReceive=${intent.action}")
            if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) {
                startAlarm(context)
            }
        }
    }

    //show custom local notification with sound when alarm triggered.
    private fun showNotification(context: Context) {

        val requestNotificationCode = 200

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 2
        val channelId = "channel_cheque_warning"
        val channelName = "Cheque Warning"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val collapsedView = RemoteViews(
            context.packageName,
            R.layout.notification_custom_collapsed_view
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(collapsedView)
            .setSound(null)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        try {
            val musicPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.loud_alarm_clock)
            musicPlayer.start()
        } catch (e: Exception) {
            Timber.d(e.toString())
        }

        val intent = Intent(context, MainActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            requestNotificationCode,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        notificationManager.notify(notificationId, mBuilder.build())
    }
}