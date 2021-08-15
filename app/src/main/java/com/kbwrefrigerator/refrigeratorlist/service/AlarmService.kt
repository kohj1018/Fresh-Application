package com.kbwrefrigerator.refrigeratorlist.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kbwrefrigerator.refrigeratorlist.MainActivity
import com.kbwrefrigerator.refrigeratorlist.R

class AlarmService : Service() {
    companion object {
        var NOTIFICATION_ID = 0
        const val PRIMARY_CHANNEL_ID = "refriger_notification_channel"
    }

    lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var runnable = Runnable {
            createNotificationChannel()
            deliverNotification(this)
        }

        val thread = Thread(runnable)
        thread.start()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_init)
                .setContentTitle("냉장고 유통기한")
                .setContentText("유통기한이 얼마 남지 않았습니다!.")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun createNotificationChannel() {   // android 상위 버전에서는 해당 알람의 채널을 따로 만들어줘야한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "냉장고 유통기한",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.description = "냉장고 유통기한 알림"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


//    companion object {
//        val ACTION_CREATE = "create"
//        val ACTION_DELETE = "delete"
//        var dataListSize: Int? = null
//    }
//
//    override fun onBind(intent: Intent): IBinder {
//        throw UnsupportedOperationException("Not yet")
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        var dataList = loadAllData()
//        dataList = dataSort(dataList)
//
//        val action = intent?.action
//        when(action) {
//
//            ACTION_CREATE -> {
//
//                for ((index, data) in dataList.withIndex()) {
//                    createAlarmThread(index, data)
//                }
//
//            }
//
//            ACTION_DELETE -> {
//                offAlarm(dataList.size)
//            }
//
//        }
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    fun createAlarmThread(index: Int, memo: Memo) {
//
////        AlarmReceiver.NOTIFICATION_ID = index   // 각각의 memo마다 다른 NOTIFICATION_ID를 갖게 하기 위함.
//
//        val runable = Runnable {
//            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//            val alarmManagerIntent = Intent(this, AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(this, AlarmReceiver.NOTIFICATION_ID, alarmManagerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//            val alarmTime = (SystemClock.elapsedRealtime() + 10 * 1000)
//            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTime, pendingIntent)
//        }
//
//        val thread = Thread(runable)
//        thread.start()
//    }
//
//    fun offAlarm(size: Int) {
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmOffIntent = Intent(this, AlarmReceiver::class.java)
//
//        for (i in 0 until size) {
//            val pendingIntent = PendingIntent.getBroadcast(this, i, alarmOffIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//            alarmManager.cancel(pendingIntent)
//        }
//    }
//
//    // 모든 데이터 로딩 함수 (loadFlattenData 함수 선언 필수)
//    fun loadAllData() : MutableList<Memo> {
//
//        /****************************************** freeze ****************************************/
//        val freezeData = loadFlattenData("freeze")
//        /******************************************************************************************/
//
//        /****************************************** fridge ****************************************/
//        val fridgeData = loadFlattenData("fridge")
//        /******************************************************************************************/
//
//        /******************************************* room *****************************************/
//        val roomData = loadFlattenData("room")
//        /******************************************************************************************/
//
//        var data = (freezeData + fridgeData + roomData).toMutableList()
//
//        return data
//    }
//
//    // Flatten 데이터 로딩 함수 (loadTabData 함수 선언 필수)
//    fun loadFlattenData(prefName: String) : MutableList<Memo> {
//
//        // 쉐어드 가져오기
//        val pref = getSharedPreferences(prefName, 0)
//
//        // tabData 가져오기
//        val tabData = loadTabData(prefName)
//
//        // data 가져오기
//        var data = mutableListOf<MutableList<Memo>>()
//        for (i in 0 until tabData.size) {
//            val dataString = pref.getString(tabData[i], "")!!   // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)
//            var midleData = ArrayList<Memo>()
//            if (dataString.isNotEmpty()) {
//                midleData = Gson().fromJson(dataString, Array<Memo>::class.java).toMutableList() as ArrayList<Memo>
//                data.add(midleData)
//            }
//        }
//
//        return data.flatten().toMutableList()   // flatten()은 리스트를 평평하게 하나의 ()안에 다 넣는 것
//    }
//
//    // tab 데이터 로딩 함수
//    fun loadTabData(prefName: String) : MutableList<String> {
//        // 쉐어드 가져오기
//        val pref = getSharedPreferences(prefName, 0)
//        val dataString = pref.getString("tab", "")!! // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)
//
//        var data = ArrayList<String>()
//
//        // 데이터 값이 있다면
//        if (dataString.isNotEmpty()) {
//            // 저장된 문자열을 -> 객체 배열로 변경
//            data = Gson().fromJson(dataString, Array<String>::class.java).toMutableList() as ArrayList<String>
//        }
//
//        return data
//    }
//
//    // 데이터 정렬 함수
//    fun dataSort(data: MutableList<Memo>) : MutableList<Memo> {
//
//        data.sortBy { it.date_long }
//
//        return data
//    }
}
