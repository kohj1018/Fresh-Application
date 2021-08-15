package com.kbwrefrigerator.refrigeratorlist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kbwrefrigerator.refrigeratorlist.service.AlarmService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_alarm.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmActivity : AppCompatActivity() {

    val ONE_DAY = (24 * 60 * 60 * 1000)

    companion object {
        lateinit var isSwitchOn: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // 쉐어드 가져오기 (switch 값 저장용)
        val loadPref = getSharedPreferences("alarm", 0)
        isSwitchOn = loadPref.getString("key", "")!!

        switch1.setChecked(isSwitchOn.toBoolean())

        var data = loadAllData()
        data = dataSort(data)
        var dataSize = data.size

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        switch1.setOnCheckedChangeListener { compoundButton, onSwitch ->
            // 스위치가 켜지면
            val toastMessage = if (onSwitch) {
                for ((index, memo) in data.withIndex()) {
                    val onAlarmIntent = Intent(this, AlarmReceiver::class.java)

                    AlarmService.NOTIFICATION_ID = index
//                    val alarmTime = (SystemClock.elapsedRealtime() + 10 * 1000)
                    val pendingIntent = PendingIntent.getBroadcast(this, AlarmService.NOTIFICATION_ID, onAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                    /********************** 알람 계산용 *********************/
                    var calendar = Calendar.getInstance()
                    val df = SimpleDateFormat("yyyy.MM.dd")
                    calendar.time = df.parse(memo.date)
                    calendar.timeInMillis = ( calendar.timeInMillis - (ONE_DAY * 3) )
                    /*****************************************************/

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
                isSwitchOn = "true"
                "알람이 켜졌습니다."
            } else {
                for (index in 0 until dataSize) {
                    val offAlarmIntent = Intent(this, AlarmReceiver::class.java)

                    AlarmService.NOTIFICATION_ID = index
                    val pendingIntent = PendingIntent.getBroadcast(this, AlarmService.NOTIFICATION_ID, offAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.cancel(pendingIntent)
                }
                isSwitchOn = "false"
                "알람이 꺼졌습니다."
            }

            // 스위치 값 저장하기
            val savePref = getSharedPreferences("alarm", 0)
            val editor = savePref.edit() // 수정모드

            editor.putString("key", isSwitchOn) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
            editor.apply()

            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // 모든 데이터 로딩 함수 (loadFlattenData 함수 선언 필수)
    fun loadAllData() : MutableList<Memo> {

        /****************************************** freeze ****************************************/
        val freezeData = loadFlattenData("freeze")
        /******************************************************************************************/

        /****************************************** fridge ****************************************/
        val fridgeData = loadFlattenData("fridge")
        /******************************************************************************************/

        /******************************************* room *****************************************/
        val roomData = loadFlattenData("room")
        /******************************************************************************************/

        var data = (freezeData + fridgeData + roomData).toMutableList()

        return data
    }

    // Flatten 데이터 로딩 함수 (loadTabData 함수 선언 필수)
    fun loadFlattenData(prefName: String) : MutableList<Memo> {

        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)

        // tabData 가져오기
        val tabData = loadTabData(prefName)

        // data 가져오기
        var data = mutableListOf<MutableList<Memo>>()
        for (i in 0 until tabData.size) {
            val dataString = pref.getString(tabData[i], "")!!   // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)
            var midleData = ArrayList<Memo>()
            if (dataString.isNotEmpty()) {
                midleData = Gson().fromJson(dataString, Array<Memo>::class.java).toMutableList() as ArrayList<Memo>
                data.add(midleData)
            }
        }

        return data.flatten().toMutableList()   // flatten()은 리스트를 평평하게 하나의 ()안에 다 넣는 것
    }

    // tab 데이터 로딩 함수
    fun loadTabData(prefName: String) : MutableList<String> {
        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)
        val dataString = pref.getString("tab", "")!! // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)

        var data = ArrayList<String>()

        // 데이터 값이 있다면
        if (dataString.isNotEmpty()) {
            // 저장된 문자열을 -> 객체 배열로 변경
            data = Gson().fromJson(dataString, Array<String>::class.java).toMutableList() as ArrayList<String>
        }

        return data
    }

    // 데이터 정렬 함수
    fun dataSort(data: MutableList<Memo>) : MutableList<Memo> {

        data.sortBy { it.date_long }

        return data
    }
}