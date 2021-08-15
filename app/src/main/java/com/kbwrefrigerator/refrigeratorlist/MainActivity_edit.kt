package com.kbwrefrigerator.refrigeratorlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_edit.*

class MainActivity_edit : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  //select back button
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_edit)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        val position = intent.getIntExtra("position", 0)

        //*************************************************************//
        //
        // 1. 데이터 로딩
        var data = loadData("temporary", "data")
        data = dataSort(data)

        for (i in 0 until data.size) {
            data[i].check = false
        }
        if (position != -1) {    // position이 -1인 경우는 메인메뉴 점세개에서 목록 삭제 눌렀을 때만
            data[position].check = true // 안전하게 하기 위해 for문으로 data의 모든 memo들의 check를 false로 바꾼 뒤 position에 해당하는 memo만 check를 true로 바꾸기
        }

        // 2. 어댑터 생성
        val adapter = CustomAdapter(this, true)
        // 3. 어댑터에 데이터 전달
        adapter.listData = data
        // 4. 화면에 있는 리사이클러뷰에 어댑터 연결
        editRecyclerView.adapter = adapter
        // 5. 레이아웃 매니저 연결
        editRecyclerView.layoutManager = LinearLayoutManager(this)
        //
        //
        //*************************************************************//

        // 데이터 삭제 (load해서 해당 값을 삭제하고 다시 save)
        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("목록 삭제")
                .setMessage("삭제된 목록은 복구되지 않습니다. 삭제하시겠습니까?")
                .setPositiveButton("예") { dialogInterface: DialogInterface, i: Int ->
                    for (i in 0 until data.size) {
                        if (adapter.listData[i].check == true) {    // adapter속의 listData의 index중 check가 true로 되어있는 것들을 삭제
                            when (data[i].loc) {
                                0 -> {
                                    var listOfdeleteItem = loadData("freeze", adapter.listData[i].category)
                                    adapter.listData[i].check = false   // load한 데이터의 해당되는 Memo요소는 check 값이 false이다. 따라서 해당 요소를 찾기 위해 false로 바꿔 놓고서 밑에서 삭제를 진행
                                    listOfdeleteItem.remove(adapter.listData[i])
                                    saveData("freeze", adapter.listData[i].category, listOfdeleteItem)
                                }
                                1 -> {
                                    var listOfdeleteItem = loadData("fridge", adapter.listData[i].category)
                                    adapter.listData[i].check = false   // load한 데이터의 해당되는 Memo요소는 check 값이 false이다. 따라서 해당 요소를 찾기 위해 false로 바꿔 놓고서 밑에서 삭제를 진행
                                    listOfdeleteItem.remove(adapter.listData[i])
                                    saveData("fridge", adapter.listData[i].category, listOfdeleteItem)
                                }
                                2 -> {
                                    var listOfdeleteItem = loadData("room", adapter.listData[i].category)
                                    adapter.listData[i].check = false   // load한 데이터의 해당되는 Memo요소는 check 값이 false이다. 따라서 해당 요소를 찾기 위해 false로 바꿔 놓고서 밑에서 삭제를 진행
                                    listOfdeleteItem.remove(adapter.listData[i])
                                    saveData("room", adapter.listData[i].category, listOfdeleteItem)
                                }
                            }
                        }
                    }
                    finish()
                }
                .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int ->  }
                .show()
        }

    }

    // 데이터 로딩 함수
    fun loadData(prefName: String, key: String) : MutableList<Memo> {
        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)
        val dataString = pref.getString(key, "")!! // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)

        var data = ArrayList<Memo>()

        // 데이터 값이 있다면
        if (dataString.isNotEmpty()) {
            // 저장된 문자열을 -> 객체 배열로 변경
            data = Gson().fromJson(dataString, Array<Memo>::class.java).toMutableList() as ArrayList<Memo>
        }

        return data
    }

    // 데이터 저장 함수
    fun saveData(prefName: String, key: String, data: MutableList<Memo>) {
        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val dataListString = Gson().toJson(data)

        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)
        val editor = pref.edit() // 수정모드

        editor.putString(key, dataListString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
        editor.apply()
    }

    // 데이터 정렬 함수
    fun dataSort(data: MutableList<Memo>) : MutableList<Memo> {
        editRecyclerView.setHasFixedSize(true)

        data.sortBy { it.date_long }

        return data
    }
}