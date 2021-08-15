package com.kbwrefrigerator.refrigeratorlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kbwrefrigerator.refrigeratorlist.service.AlarmService

class AlarmReceiver(name: String) : BroadcastReceiver() {
//    companion object {
//        var NOTIFICATION_ID = 0
//        const val PRIMARY_CHANNEL_ID = "refriger_notification_channel"
//    }
//
//    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {

        val intent = Intent(context, AlarmService::class.java)
        context.startService(intent)
//        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        createNotificationChannel()
//        deliverNotification(context)
    }

//    private fun deliverNotification(context: Context) {
//        val contentIntent = Intent(context, MainActivity::class.java)
//        val contentPendingIntent = PendingIntent.getActivity(
//            context,
//            NOTIFICATION_ID,
//            contentIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val builder =
//            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_init)
//                .setContentTitle("냉장고 유통기한")
//                .setContentText("OOO의 유통기한이 O일 남았습니다.")
//                .setContentIntent(contentPendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//
//        notificationManager.notify(NOTIFICATION_ID, builder.build())
//    }
//
//    fun createNotificationChannel() {   // android 상위 버전에서는 해당 알람의 채널을 따로 만들어줘야한다.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                PRIMARY_CHANNEL_ID,
//                "냉장고 유통기한",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.YELLOW
//            notificationChannel.enableVibration(true)
//            notificationChannel.description = "냉장고 유통기한 알림"
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }
}