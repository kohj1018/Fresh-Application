package com.kbwrefrigerator.refrigeratorlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class DetailActivity : AppCompatActivity() {

    val ONE_DAY = (24 * 60 * 60 * 1000)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.modifyBtn -> {
                val modifyIntent = Intent(this, ModifyActivity::class.java)
                modifyIntent.putExtra("name", detail_textName.text)
                modifyIntent.putExtra("date", detail_textDate.text)
                modifyIntent.putExtra("category", detail_textCategory.text)
                modifyIntent.putExtra("uriString", intent.getStringExtra("uriString"))    // 수정할 때 수정할 아이템의 사진에 접근하기 위해 보내는 값(CustomAdapter에서 보내준 값을 다시 보내줌)
                when (detail_textLoc.text) {
                    "냉동" -> {
                        modifyIntent.putExtra("loc", 0)
                    }
                    "냉장" -> {
                        modifyIntent.putExtra("loc", 1)
                    }
                    "실온" -> {
                        modifyIntent.putExtra("loc", 2)
                    }
                }
                startActivity(modifyIntent)
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var detail_textRemain: TextView? = null
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        // 데이터 받아서 이름, 날짜, 디데이등 표시
        detail_textName.text = intent.getStringExtra("name")
        detail_textDate.text = intent.getStringExtra("date")
        detail_textDday.text = intent.getStringExtra("Dday")
        detail_textLoc.text = intent.getStringExtra("loc")
        detail_textCategory.text = intent.getStringExtra("category")
        val date_long = intent.getLongExtra("date_long", 0L)

        /********************** 실시간 유통기한 몇초 남았는지 나타내는 쓰레드 ************************/
        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val cal = Calendar.getInstance()

                //********** 디데이를 기점으로 지났는지 지나지 않았는지를 분기하여 처리 *********//
                val diff = ( date_long - System.currentTimeMillis() )
                if (diff >= 0) {
                    val sf = SimpleDateFormat("dd일 HH시간 mm분 ss초전")
                    cal.timeInMillis = diff
                    val stringTime = sf.format(cal.time)

                    detail_textRemain = findViewById(R.id.detail_textRemain)
                    detail_textRemain?.setText(stringTime)
                } else {
                    val sf = SimpleDateFormat("dd일 HH시간 mm분 ss초지남")
                    cal.timeInMillis = ( System.currentTimeMillis() - date_long )               // 수정 필요
                    val stringTime = sf.format(cal.time)

                    detail_textRemain = findViewById(R.id.detail_textRemain)
                    detail_textRemain?.setText(stringTime)
                }
                //****************************************************************//
            }
        }

        thread(start = true) {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    // Thread.sleep(), Future.get(),
                    // BlockingQueue.take() 등이 여기올 수 있다.
                    Thread.sleep(1000)
                    mHandler?.sendEmptyMessage(0)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        /*********************************************************************************/
    }
}