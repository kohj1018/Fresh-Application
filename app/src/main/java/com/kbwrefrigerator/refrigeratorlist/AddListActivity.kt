package com.kbwrefrigerator.refrigeratorlist

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_list.*
import java.text.SimpleDateFormat
import java.util.*

var addListLocation = 10
var addListTabData = mutableListOf("냉동,냉장,실온을 먼저 체크해주세요")
var uriString = "android.resource://com.example.refrigeratorlist/drawable/ic_init"

class AddListActivity : AppCompatActivity() {

    val ONE_DAY = (24 * 60 * 60 * 1000)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.addlist_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // 저장 버튼 클릭시
            R.id.saveBtn -> {
                if (nameText.text.toString() == "") {
                    Toast.makeText(this, "식재료 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (dateView.text.toString() == "2XXX.XX.XX") {
                    Toast.makeText(this, "유통기한을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (categoryView.text.toString() == "") {
                    Toast.makeText(this, "체크한 곳에 카테고리가 없습니다. 카테고리를 먼저 추가해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    var name = nameText.text.toString()
                    var date = dateView.text.toString()
                    var category = categoryView.text.toString()
                    var loc = 3
                    when {
                        freezeCheckBox.isChecked == true -> {
                            loc = 0
                        }
                        fridgeCheckBox.isChecked == true -> {
                            loc = 1
                        }
                        roomCheckBox.isChecked == true -> {
                            loc = 2
                        }
                    }
                    /****************** D - day 계산용 *********************/
                    // D-day 계산용 Long Type의 날짜를 저장
                    var calendar = Calendar.getInstance()
                    val df = SimpleDateFormat("yyyy.MM.dd")
                    calendar.time = df.parse(dateView.text.toString())!!    // dateView에 작성되어있는 string을 이용
                    var date_long = calendar.timeInMillis
                    /*****************************************************/

                    var memo = Memo(name, date, date_long, loc, category, false, uriString)

                    when (loc) {
                        0 -> {
                            saveMemoData("freeze", category, memo)
                        }
                        1 -> {
                            saveMemoData("fridge", category, memo)
                        }
                        2 -> {
                            saveMemoData("room", category, memo)
                        }
                    }

                    addListLocation = 10
                    addListTabData = mutableListOf("냉동,냉장,실온을 먼저 체크해주세요")
                    uriString = "android.resource://com.example.refrigeratorlist/drawable/ic_init"
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
        setContentView(R.layout.activity_add_list)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        // 사진란에 초기이미지
        if (uriString == "android.resource://com.example.refrigeratorlist/drawable/ic_init") {
            cropImageView.setImageUriAsync(Uri.parse(uriString))
            cropImageView.isShowCropOverlay = false
        }

        // 냉동, 냉장, 실온 체크 + tab 데이터 불러오기
        if (intent.hasExtra("refreshAddListloc") || intent.hasExtra("loc")) {
            // 현재 액티비티에서 누른 loc 값(refreshAddListloc)을 우선적으로 가지되, 누른게 없다면 타고 들어온 액티비티의 loc 값(loc)을 디폴트로 사용함
            addListLocation = intent.getIntExtra("refreshAddListloc", intent.getIntExtra("loc", 3))
            when (addListLocation) {
                0 -> {
                    freezeCheckBox.isChecked = true
                    addListTabData = loadTabData("freeze")
                }
                1 -> {
                    fridgeCheckBox.isChecked = true
                    addListTabData = loadTabData("fridge")
                }
                2 -> {
                    roomCheckBox.isChecked = true
                    addListTabData = loadTabData("room")
                }
            }

            // tab 데이터가 비어있을 경우 카테고리를 먼저 추가해달라고 다이얼로그 띄우고 editTabActivity로 이동
            if (addListTabData.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("카테고리 추가")
                    .setMessage("생성된 카테고리가 없습니다. 카테고리를 먼저 추가해주세요.")
                    .setPositiveButton("예") { dialogInterface: DialogInterface, i: Int ->
                        val editTabIntent = Intent(this, EditTabActivity::class.java)
                        editTabIntent.putExtra("loc", addListLocation)
                        startActivity(editTabIntent)
                        finish()
                    }
                    .show()
            }
        }
        freezeCheckBox.setOnClickListener{
            fridgeCheckBox.isChecked = false
            roomCheckBox.isChecked = false
            refresh(0)
        }
        fridgeCheckBox.setOnClickListener {
            freezeCheckBox.isChecked = false
            roomCheckBox.isChecked = false
            refresh(1)
        }
        roomCheckBox.setOnClickListener {
            freezeCheckBox.isChecked = false
            fridgeCheckBox.isChecked = false
            refresh(2)
        }

        // 달력 버튼 눌렀을 때 (DatePicker로 달력 표시)
        dateBtn.setOnClickListener {
            if (freezeCheckBox.isChecked == false && fridgeCheckBox.isChecked == false && roomCheckBox.isChecked == false) {
                Toast.makeText(this, "냉동, 냉장, 실온을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val calendar = Calendar.getInstance()
                var year = calendar.get(Calendar.YEAR)
                var month = calendar.get(Calendar.MONTH)
                var day = calendar.get(Calendar.DAY_OF_MONTH)

                var date_listener = object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        dateView.text = "${year}.%02d.%02d".format(month+1, dayOfMonth)             // 다른건 그대로 나오는데 month는 시작값이 0이기 때문에 1을 더해줘야한다.
                    }
                }

                // DatePickerDialog 뒤에 .apply{} 부분은 현재 시간을 기준으로 그것보다 minDate는 선택할 수 없게 비활성화 하는 기능
                // System.currentTimeMillis는 1/1000초 단위로 현재시간이 long Type으로 반환된다. 때문에 뒤에 ONE_DAY(= 24 * 60 * 60 * 1000) * N 을 더해줘 N일 뒤부터 날짜 선택이 가능하도록 한다.
                var picker = DatePickerDialog(this, date_listener, year, month, day).apply { datePicker.minDate = System.currentTimeMillis() + ONE_DAY * 1 }
                picker.show()
            }
        }

        // 카테고리 spinner
        if (freezeCheckBox.isChecked == false && fridgeCheckBox.isChecked == false && roomCheckBox.isChecked == false) {
            addListTabData = mutableListOf("냉동,냉장,실온을 먼저 체크해주세요")
        }
        val comparisonTarget = mutableListOf("냉동,냉장,실온을 먼저 체크해주세요")       // 이거 조금 비효율적임. 방법 찾기!!
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, addListTabData)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if ( addListTabData == comparisonTarget ) {
                    categoryView.text  = ""
                } else {
                    categoryView.text = addListTabData[p2]
                }
            }
        }

        // 사진 가져오기
        addImageBtn.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1,1)
                .start(this)
        }

        // Name 먼저 적으려고 할 때 위에 냉동, 냉장, 실온 먼저 고르라고 하는 거      (이거 안됌 수정 필요)
        nameText.setOnClickListener {
            if (freezeCheckBox.isChecked == false && fridgeCheckBox.isChecked == false && roomCheckBox.isChecked == false) {
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
        refreshIntent.putExtra("refreshAddListloc", loc)
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

    // 데이터 저장 함수 (loadData 함수 선언 필수)
    fun saveMemoData(prefName: String, key: String, memo: Memo) {
        var data = loadData(prefName, key)
        data.add(memo)
        // 매개변수로 들어온 배열을 -> 문자열로 변환
        val dataString = Gson().toJson(data)

        // 쉐어드 가져오기
        val pref = getSharedPreferences(prefName, 0)
        val editor = pref.edit() // 수정모드

        editor.putString(key, dataString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
        editor.apply()
    }

    // 가져온 사진 데이터 받아오기(+ memo.uriString에 들어갈 값 넣기)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                val resultUri = result.uri
                uriString = resultUri.toString()

                cropImageView.setImageUriAsync(resultUri)
                cropImageView.isShowCropOverlay = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}
