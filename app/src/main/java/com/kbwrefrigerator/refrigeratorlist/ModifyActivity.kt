package com.kbwrefrigerator.refrigeratorlist

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_modify.*
import java.text.SimpleDateFormat
import java.util.*

var modifyComparisonMemo = Memo("","",0, 3, "", false, "")
var modifyLocation = 10
var modifyTabData = mutableListOf<String>()
var modifyLocString = ""
var modifyUriString = "android.resource://com.example.refrigeratorlist/drawable/ic_init"

class ModifyActivity : AppCompatActivity() {

    val ONE_DAY = (24 * 60 * 60 * 1000)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.modify_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // 저장 버튼 클릭시
            R.id.savemodifyBtn -> {
                if (modifyNameText.text.toString() == "") {
                    Toast.makeText(this, "식재료 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (modifyCategoryView.text.toString() == "") {
                    Toast.makeText(this, "체크한 곳에 카테고리가 없습니다. 카테고리를 먼저 추가해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    var name = modifyNameText.text.toString()
                    var date = modifyDateView.text.toString()
                    var category = modifyCategoryView.text.toString()
                    var loc = 3
                    when {
                        modifyFreezeCheckBox.isChecked == true -> {
                            loc = 0
                        }
                        modifyFridgeCheckBox.isChecked == true -> {
                            loc = 1
                        }
                        modifyRoomCheckBox.isChecked == true -> {
                            loc = 2
                        }
                    }
                    /****************** D - day 계산용 *********************/
                    // D-day 계산용 Long Type의 날짜를 저장
                    var calendar = Calendar.getInstance()
                    val df = SimpleDateFormat("yyyy.MM.dd")
                    calendar.time = df.parse(modifyDateView.text.toString())!!    // dateView에 작성되어있는 string을 이용
                    var date_long = calendar.timeInMillis
                    /*****************************************************/

                    var memo = Memo(name, date, date_long, loc, category, false, modifyUriString)

                    when (loc) {
                        0 -> {
                            // 앞에 두개는 수정하기 전 데이터가 있던 곳의 prefName, key 값이고 뒤에 두개는 수정 후 데이터가 들어갈 prefName, key 값이다. 마지막은 넣을 memo값
                            editMemoData(modifyLocString, modifyComparisonMemo.category, "freeze", category, memo)
                        }
                        1 -> {
                            // 앞에 두개는 수정하기 전 데이터가 있던 곳의 prefName, key 값이고 뒤에 두개는 수정 후 데이터가 들어갈 prefName, key 값이다. 마지막은 넣을 memo값
                            editMemoData(modifyLocString, modifyComparisonMemo.category, "fridge", category, memo)
                        }
                        2 -> {
                            // 앞에 두개는 수정하기 전 데이터가 있던 곳의 prefName, key 값이고 뒤에 두개는 수정 후 데이터가 들어갈 prefName, key 값이다. 마지막은 넣을 memo값
                            editMemoData(modifyLocString, modifyComparisonMemo.category, "room", category, memo)
                        }
                    }

                    // 끝날 때 전역변수들 초기 값으로 초기화
                    modifyComparisonMemo = Memo("","",0, 3, "", false, "")
                    modifyLocation = 10
                    modifyTabData = mutableListOf<String>()
                    modifyLocString = ""
                    modifyUriString = "android.resource://com.example.refrigeratorlist/drawable/ic_init"
                    finishAffinity()
                    val gotoMainIntent = Intent(this, MainActivity::class.java)
                    startActivity(gotoMainIntent)
                    finish()
                }
            }
            android.R.id.home -> {  //select back button
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)


        // 수정할 아이템의 데이터 받아오기
        modifyNameText.setText(intent.getStringExtra("name"))
        modifyDateView.text = intent.getStringExtra("date")
        modifyUriString = intent.getStringExtra("uriString").toString()

        modifyCropImageView.setImageUriAsync(Uri.parse(intent.getStringExtra("uriString"))) // DetailActivity에서 받아온 uriString을 이용해 사진을 불러오기
        modifyCropImageView.isShowCropOverlay = false

        if ( !intent.hasExtra("refreshModifyloc") ) {          // refresh 되지 않았을 때만 실행, 즉 처음 ModifyActivity에 들어왔을 때만 실행
            /*********** date_long 계산용 ************/
            // D-day 계산용 Long Type의 날짜를 저장
            var calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy.MM.dd")
            calendar.time = df.parse( intent.getStringExtra("date") )!!    // intent로 받아온 string을 이용
            var date_long = calendar.timeInMillis
            /****************************************/

            modifyComparisonMemo = Memo(
                intent.getStringExtra("name"),
                intent.getStringExtra("date"),
                date_long,
                intent.getIntExtra("loc", 3),
                intent.getStringExtra("category")!!,
                false,
                intent.getStringExtra("uriString")!!
            )
            when (modifyComparisonMemo.loc) {
                0 -> modifyLocString = "freeze"
                1 -> modifyLocString = "fridge"
                2 -> modifyLocString = "room"
            }
        }

        // 냉동, 냉장, 실온 체크 + tab 데이터 불러오기
        if (intent.hasExtra("refreshModifyloc") || intent.hasExtra("loc")) {
            // 현재 액티비티에서 누른 loc 값(refreshModfiyloc)을 우선적으로 가지되, 누른게 없다면 타고 들어온 액티비티의 loc 값(loc)을 디폴트로 사용함
            modifyLocation = intent.getIntExtra("refreshModifyloc", intent.getIntExtra("loc", 3))
            when (modifyLocation) {
                0 -> {
                    modifyFreezeCheckBox.isChecked = true
                    modifyTabData = loadTabData("freeze")
                }
                1 -> {
                    modifyFridgeCheckBox.isChecked = true
                    modifyTabData = loadTabData("fridge")
                }
                2 -> {
                    modifyRoomCheckBox.isChecked = true
                    modifyTabData = loadTabData("room")
                }
            }
        }
        modifyFreezeCheckBox.setOnClickListener{
            modifyFridgeCheckBox.isChecked = false
            modifyRoomCheckBox.isChecked = false
            refresh(0)
        }
        modifyFridgeCheckBox.setOnClickListener {
            modifyFreezeCheckBox.isChecked = false
            modifyRoomCheckBox.isChecked = false
            refresh(1)
        }
        modifyRoomCheckBox.setOnClickListener {
            modifyFreezeCheckBox.isChecked = false
            modifyFridgeCheckBox.isChecked = false
            refresh(2)
        }

        // 달력 버튼 눌렀을 때 (DatePicker로 달력 표시)
        modifyDateBtn.setOnClickListener {
            if (modifyFreezeCheckBox.isChecked == false && modifyFridgeCheckBox.isChecked == false && modifyRoomCheckBox.isChecked == false) {
                Toast.makeText(this, "냉동, 냉장, 실온을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val calendar = Calendar.getInstance()
                var year = calendar.get(Calendar.YEAR)
                var month = calendar.get(Calendar.MONTH)
                var day = calendar.get(Calendar.DAY_OF_MONTH)

                var date_listener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        modifyDateView.text = "${year}.%02d.%02d".format(month+1, dayOfMonth)             // 다른건 그대로 나오는데 month는 시작값이 0이기 때문에 1을 더해줘야한다.
                    }
                }

                // DatePickerDialog 뒤에 .apply{} 부분은 현재 시간을 기준으로 그것보다 minDate는 선택할 수 없게 비활성화 하는 기능
                // System.currentTimeMillis는 1/1000초 단위로 현재시간이 long Type으로 반환된다. 때문에 뒤에 ONE_DAY(= 24 * 60 * 60 * 1000) * N 을 더해줘 N일 뒤부터 날짜 선택이 가능하도록 한다.
                var picker = DatePickerDialog(this, date_listener, year, month, day).apply { datePicker.minDate = System.currentTimeMillis() + ONE_DAY * 1 }
                picker.show()
            }
        }

        // 카테고리 spinner
        if (modifyFreezeCheckBox.isChecked == false && modifyFridgeCheckBox.isChecked == false && modifyRoomCheckBox.isChecked == false) {

        }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, modifyTabData)
        modifySpinner.adapter = spinnerAdapter
        modifySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if ( intent.hasExtra("refreshModifyloc") ) {
                    modifyCategoryView.text = modifyTabData[p2]
                } else if ( modifyLocation == intent.getIntExtra("loc", 3) ) {
                    modifyCategoryView.text = intent.getStringExtra("category")
                    modifyLocation = 10
                } else {
                    modifyCategoryView.text = modifyTabData[p2]
                }
            }
        }

        // 사진 가져오기
        modifyAddImageBtn.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1,1)
                .start(this)
        }

        // Name 먼저 적으려고 할 때 위에 냉동, 냉장, 실온 먼저 고르라고 하는 거      (이거 안됌 수정 필요)
        modifyNameText.setOnClickListener {
            if (modifyFreezeCheckBox.isChecked == false && modifyFridgeCheckBox.isChecked == false && modifyRoomCheckBox.isChecked == false) {
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

    // 액티비티 새로고침 함수
    fun refresh(loc: Int) {
        val refreshIntent = getIntent()
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        refreshIntent.putExtra("refreshModifyloc", loc)
        finish()
        startActivity(refreshIntent)
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

    // 데이터 수정 함수 (loadData 함수 선언 필수)
    fun editMemoData(lastPrefName: String, lastKey: String, newPrefName: String, newKey: String, memo: Memo) {
        var lastData = loadData(lastPrefName, lastKey)

        // DetailActivity에서 받은 값들로 전역변수 comparisonMemo를 배정해준뒤, 그 값을 비교해 찾고 삭제후 저장
        lastData.remove(modifyComparisonMemo)

        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val lastDataString = Gson().toJson(lastData)

        // 쉐어드 가져오기
        val lastPref = getSharedPreferences(lastPrefName, 0)
        val lastEditor = lastPref.edit() // 수정모드

        lastEditor.putString(lastKey, lastDataString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
        lastEditor.apply()

        /************************************ 구분선 ***********************************/

        var newData = loadData(newPrefName, newKey)

        // 수정해서 들어가게 되는 MutableList를 불러온 다음 그곳에 저장
        newData.add(memo)

        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val newDataString = Gson().toJson(newData)

        // 쉐어드 가져오기
        val newPref = getSharedPreferences(newPrefName, 0)
        val newEditor = newPref.edit() // 수정모드

        newEditor.putString(newKey, newDataString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
        newEditor.apply()
    }

    // 가져온 사진 데이터 받아오기(+ memo.uriString에 들어갈 값 넣기)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                val resultUri = result.uri
                modifyUriString = resultUri.toString()

                modifyCropImageView.setImageUriAsync(resultUri)
                modifyCropImageView.isShowCropOverlay = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}