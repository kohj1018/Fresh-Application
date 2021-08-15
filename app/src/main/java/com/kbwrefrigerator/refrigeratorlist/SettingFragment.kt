package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {

    // fragment에서 사용할 Context
    private lateinit var settingFragContext: Context

    // fragment에서 사용할 View
    private lateinit var settingFragViewOfLayout: View

    // fragment에서 사용할 context를 얻는 과정(기본적으로 fragment는 Activity가 아니기때문에 context가 없다.)
    // onAttach lifeCycle에서 얻는 것이 getActivity를 이용한 방법보다 안전한 방법이다.(context가 가끔 null 이 되기도 한다.)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingFragContext = context    // Root Activity에 접근할 경우 context as Activity 를 이용
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingFragViewOfLayout =  inflater.inflate(R.layout.fragment_setting, container, false)

        // toolbar
        var toolbar = settingFragViewOfLayout.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (settingFragContext as MainActivity).setSupportActionBar(toolbar)  // mainFragContext context로 MainActivity를 불러와 사용
        var actionBar = (settingFragContext as MainActivity).supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌

        val arrayOfSetting = arrayListOf("알람 설정", "전체 초기화", "버전 v1.0.0", "앱 정보")
        val settingAdapter = ArrayAdapter(settingFragContext, android.R.layout.simple_list_item_1, arrayOfSetting)
        settingFragViewOfLayout.settingListView.adapter = settingAdapter

        settingFragViewOfLayout.settingListView.setOnItemClickListener { adapterView, view, position, id ->
            when (position) {
                0 -> {  // 알람 설정
//                    val alarmIntent = Intent(settingFragContext, AlarmActivity::class.java)
//                    startActivity(alarmIntent)
                    Toast.makeText(settingFragContext, "업데이트 중입니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {  // 쉐어드프리퍼런스 전체 초기화
                    AlertDialog.Builder(settingFragContext)
                    .setTitle("경고")
                    .setMessage("초기화하면 카테고리를 포함한 모든 내용이 초기화됩니다. 초기화 하시겠습니까?")
                    .setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        val freezePref = (settingFragContext as MainActivity).getSharedPreferences("freeze", 0)
                        val freezeEditor = freezePref.edit()
                        freezeEditor.clear()
                        freezeEditor.commit()

                        val fridgePref = (settingFragContext as MainActivity).getSharedPreferences("fridge", 0)
                        val fridgeEditor = fridgePref.edit()
                        fridgeEditor.clear()
                        fridgeEditor.commit()

                        val roomPref = (settingFragContext as MainActivity).getSharedPreferences("room", 0)
                        val roomEditor = roomPref.edit()
                        roomEditor.clear()
                        roomEditor.commit()

                        val temporaryPref = (settingFragContext as MainActivity).getSharedPreferences("temporary", 0)
                        val temporaryEditor = temporaryPref.edit()
                        temporaryEditor.clear()
                        temporaryEditor.commit()

                        // 앱을 재시작
                        getActivity()?.finishAffinity()
                        val intent = Intent(settingFragContext, MainActivity::class.java)
                        startActivity(intent)
                        System.exit(0)
                    }
                    .setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }
                    .show()
                }
                3 -> {  // 앱 정보(만든이)
                    val madeByIntent = Intent(settingFragContext, MadeByActivity::class.java)
                    startActivity(madeByIntent)
                }
            }
        }

        return settingFragViewOfLayout
    }
}