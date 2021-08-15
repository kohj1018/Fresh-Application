package com.kbwrefrigerator.refrigeratorlist

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_tab.*

var editLocation = 0
var editTabData = mutableListOf<String>()

class EditTabActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tab)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        // 냉동, 냉장, 실온 체크 + tab 데이터 불러오기
        if (intent.hasExtra("refreshEditTabloc") || intent.hasExtra("loc")) {
            editLocation = intent.getIntExtra("refreshEditTabloc", intent.getIntExtra("loc", 3))    // 갱신됐으면 갱신된로 loc 값으로, 안됐으면 받아온 loc 값으
            when (editLocation) {
                0 -> {
                    editTabfreezeCheckBox.isChecked = true
                    editTabData = loadTabData("freeze")
                }
                1 -> {
                    editTabfridgeCheckBox.isChecked = true
                    editTabData = loadTabData("fridge")
                }
                2 -> {
                    editTabroomCheckBox.isChecked = true
                    editTabData = loadTabData("room")
                }
            }
        }
        editTabfreezeCheckBox.setOnClickListener{
            editTabfridgeCheckBox.isChecked = false
            editTabroomCheckBox.isChecked = false
            refresh(0)
        }
        editTabfridgeCheckBox.setOnClickListener {
            editTabfreezeCheckBox.isChecked = false
            editTabroomCheckBox.isChecked = false
            refresh(1)
        }
        editTabroomCheckBox.setOnClickListener {
            editTabfreezeCheckBox.isChecked = false
            editTabfridgeCheckBox.isChecked = false
            refresh(2)
        }

        // 추가 버튼 누를때
        addTabBtn.setOnClickListener {
            if (editTabfreezeCheckBox.isChecked == false && editTabfridgeCheckBox.isChecked == false && editTabroomCheckBox.isChecked == false) {
                Toast.makeText(this, "냉동, 냉장, 실온을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else if (editTabNameText.text.toString() == "") {
                Toast.makeText(this, "카테고리 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if ( editTabData.find { it == editTabNameText.text.toString() } == editTabNameText.text.toString() ) {   // editTabNameText에 작성한 카테고리 이름이 있는지 확인하고 있으면 Toast 메시지 발송
                Toast.makeText(this, "중복된 카테고리가 있습니다.", Toast.LENGTH_SHORT).show()
            } else {
                editTabData.remove(" --빈 카테고리-- ")  // 배열에 해당 요소값을 찾아 삭제, 없으면 값을 유지하며 false를 반환
                when {
                    editTabfreezeCheckBox.isChecked -> {
                        editTabData.add(editTabNameText.text.toString())
                        saveTabData("freeze", editTabData)
                        Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        refresh(0)
                    }
                    editTabfridgeCheckBox.isChecked -> {
                        editTabData.add(editTabNameText.text.toString())
                        saveTabData("fridge", editTabData)
                        Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        refresh(1)
                    }
                    editTabroomCheckBox.isChecked -> {
                        editTabData.add(editTabNameText.text.toString())
                        saveTabData("room", editTabData)
                        Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        refresh(2)
                    }
                }
            }
        }

        // 카테고리 spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, editTabData)
        editTabSpinner.adapter = spinnerAdapter
        editTabSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                // 삭제 버튼 누를때
                delTabBtn.setOnClickListener {
                    if (editTabfreezeCheckBox.isChecked == false && editTabfridgeCheckBox.isChecked == false && editTabroomCheckBox.isChecked == false) {
                        Toast.makeText(this@EditTabActivity, "냉동, 냉장, 실온을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        AlertDialog.Builder(this@EditTabActivity)
                            .setTitle("카테고리 삭제")
                            .setMessage("카테고리 삭제 시 카테고리에 속한 목록 또한 모두 삭제되며 이는 복구되지 않습니다. 삭제하시겠습니까?")
                            .setPositiveButton("예") { dialogInterface: DialogInterface, i: Int ->
                                when (editLocation) {
                                    0 -> {
                                        delTabData("freeze", editTabData[p2])
                                        editTabData.removeAt(p2)
                                        saveTabData("freeze", editTabData)
                                    }
                                    1 -> {
                                        delTabData("fridge", editTabData[p2])
                                        editTabData.removeAt(p2)
                                        saveTabData("fridge", editTabData)
                                    }
                                    2 -> {
                                        delTabData("room", editTabData[p2])
                                        editTabData.removeAt(p2)
                                        saveTabData("room", editTabData)
                                    }
                                }
                                Toast.makeText(this@EditTabActivity, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                refresh(editLocation)
                            }
                            .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int ->  }
                            .show()
                    }
                }
            }
        }

        // Name 먼저 적으려고 할 때 위에 냉동, 냉장, 실온 먼저 고르라고 하는 거  (이거 안됌 수정 필요)
        editTabNameText.setOnClickListener {
            if (editTabfreezeCheckBox.isChecked == false && editTabfridgeCheckBox.isChecked == false && editTabroomCheckBox.isChecked == false) {
                Toast.makeText(this, "냉동, 냉장, 실온을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
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

    // tab 데이터 저장 함수
    fun saveTabData(prefName: String, data: MutableList<String>) {
        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val dataString = Gson().toJson(data)

        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)
        val editor = pref.edit() // 수정모드

        editor.putString("tab", dataString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
        editor.apply()
    }

    // tab 데이터 삭제 함수
    fun delTabData(prefName: String, key: String) {
        val pref = getSharedPreferences(prefName, 0)
        val editor = pref.edit()
        editor.remove(key)
        editor.commit()
    }

    // 액티비티 새로고침 함수
    fun refresh(loc: Int) {
        val refreshIntent = getIntent()
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        refreshIntent.putExtra("refreshEditTabloc", loc)
        finish()
        startActivity(refreshIntent)
    }
}