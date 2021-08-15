package com.kbwrefrigerator.refrigeratorlist

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_fridge.*

class FridgeActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.fridge_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fridgeEditTabBtn -> {
                val editTabIntent = Intent(this, EditTabActivity::class.java)
                editTabIntent.putExtra("loc", 1)
                startActivity(editTabIntent)
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fridge)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        // 목록 추가 버튼 클릭시
        fridgeAddlistBtn.setOnClickListener {
            val addIntent = Intent(this, AddListActivity::class.java)
            addIntent.putExtra("loc",1)
            startActivity(addIntent)
        }

        //*************************************************************//
        //
        // 1. tab 데이터 로딩
        var tabData = loadTabData("fridge")

            // tab 데이터가 비어있을 경우 카테고리를 먼저 추가해달라고 다이얼로그 띄우고 editTabActivity로 이동
        if (tabData.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("카테고리 추가")
                .setMessage("생성된 카테고리가 없습니다. 카테고리를 먼저 추가해주세요.")
                .setPositiveButton("예") { dialogInterface: DialogInterface, i: Int ->
                    val editTabIntent = Intent(this, EditTabActivity::class.java)
                    editTabIntent.putExtra("loc", 1)
                    startActivity(editTabIntent)
                }
                .show()
        }
        // 2. 어댑터 생성 및 데이터 전달
        val viewPagerAdapter = CustomViewPagerAdapter(this, "fridge", tabData)
        // 3. 화면에 있는 뷰페이저에 어댑터 연결
        viewPager1.adapter = viewPagerAdapter
        //
        //
        //*************************************************************//
        TabLayoutMediator(tabLayout, viewPager1) { tab, position ->
            tab.text = tabData[position]
        }.attach()

    }

    // 액티비티 재실행시 갱신
    override fun onRestart() {
        super.onRestart()
        refresh()
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

    // 액티비티 새로고침 함수
    fun refresh() {
        val refreshIntent = getIntent()
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(refreshIntent)
    }
}