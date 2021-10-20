package com.example.alarmmanagerex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver:BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        // 채널을 생성하고 알람이 오면 노티피케이션 알람을 띄어줘야함.
        createNotificationChannel(context)
        notifyAlarm(context)

    }

    private fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun notifyAlarm(context: Context){

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle("알람")
            .setContentText("알람이 울립니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFY_ID, builder.build())
    }

    companion object{
        private const val NOTIFY_ID = 100
        private const val CHANNEL_ID = "100"
        private const val CHANNEL_NAME = "Alarm"
    }

}
