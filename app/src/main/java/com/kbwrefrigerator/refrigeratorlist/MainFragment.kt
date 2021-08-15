package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment : Fragment() {

    // fragment에서 사용할 Context
    private var mainFragContext: Context? = null

    // fragment에서 사용할 View
    private lateinit var mainFragViewOfLayout: View

    // fragment에서 사용할 context를 얻는 과정(기본적으로 fragment는 Activity가 아니기때문에 context가 없다.)
    // onAttach lifeCycle에서 얻는 것이 getActivity를 이용한 방법보다 안전한 방법이다.(context가 가끔 null 이 되기도 한다.)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainFragContext = context   // Root Activity에 접근할 경우 context as Activity 를 이용
    }

    // RecyclerView에 넣을 때 쓰는 데이터 변수를 클래스 변수로 선언
    var data = mutableListOf<Memo>()

    // RecyclerView의 어댑터 변수도 클래스 변수로 선언
    var adapter: CustomAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        /******************************** search 기능 ********************************/
        val menuItem = menu.findItem(R.id.searchItem)

        val searchItem = menuItem.actionView as SearchView

        searchItem.maxWidth = Int.MAX_VALUE
        searchItem.queryHint = "검색"

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // 사용자가 쿼리를 제출할 때 호출됩니다.
            override fun onQueryTextSubmit(query: String?): Boolean {   // query는 filterString이다.
                adapter!!.filter.filter(query)
                return true
            }

            // 사용자가 쿼리 텍스트를 변경하면 호출됩니다.
            override fun onQueryTextChange(newText: String?): Boolean { // newText는 filterString이다.
                adapter!!.filter.filter(newText)
                return true
            }
        })
        /****************************************************************************/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editDelete -> {
                val editIntent = Intent(mainFragContext, MainActivity_edit::class.java)
                editIntent.putExtra("position", -1)
                startActivity(editIntent)
            }
            R.id.editTab -> {
                val editTabIntent = Intent(mainFragContext, EditTabActivity::class.java)
                startActivity(editTabIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // setHasOptionsMenu(true) 하기 위해서 오버라이드. setHasOptionsMenu 해줘야 onOptionsItemSelected에서 오류 안남.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainFragViewOfLayout = inflater.inflate(R.layout.fragment_main, container, false)

        // toolbar
        var toolbar = mainFragViewOfLayout.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (mainFragContext as MainActivity).setSupportActionBar(toolbar)  // mainFragContext context로 MainActivity를 불러와 사용
        var actionBar = (mainFragContext as MainActivity).supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌

        mainFragViewOfLayout.freezeCount.text = dataSize("freeze").toString()
        mainFragViewOfLayout.fridgeCount.text = dataSize("fridge").toString()
        mainFragViewOfLayout.roomCount.text = dataSize("room").toString()

        // 냉장, 냉동, 실온 버튼 클릭시
        mainFragViewOfLayout.freezeBtn.setOnClickListener {
            val freezeIntent = Intent(mainFragContext, FreezeActivity::class.java)
            startActivity(freezeIntent)
        }
        mainFragViewOfLayout.fridgeBtn.setOnClickListener {
            val fridgeIntent = Intent(mainFragContext, FridgeActivity::class.java)
            startActivity(fridgeIntent)
        }
        mainFragViewOfLayout.roomBtn.setOnClickListener {
            val roomIntent = Intent(mainFragContext, RoomActivity::class.java)
            startActivity(roomIntent)
        }

        // 목록 추가 버튼 클릭시
        mainFragViewOfLayout.addlistBtn.setOnClickListener {
            val addIntent = Intent(mainFragContext, AddListActivity::class.java)
            startActivity(addIntent)
        }

        //*************************************************************//
        //
        // 1. 데이터 로딩
        data = loadAllData()
        data = dataSort(data)
        // 2. 어댑터 생성
        adapter = CustomAdapter(mainFragContext as MainActivity, false)
        // 3. 어댑터에 데이터 전달
        adapter!!.setData(data) // setData : search 기능 이용시 사용하는 함수. MainActivity에만 search기능이 있기때문에 여기서만 data을 이렇게 준다. listDataFilter에도 데이터를 한번에 넣어주기 위해 편의상 만든 거
        // 4. 화면에 있는 리사이클러뷰에 어댑터 연결
        mainFragViewOfLayout.mainRecyclerView.adapter = adapter
        // 5. 레이아웃 매니저 연결
        mainFragViewOfLayout.mainRecyclerView.layoutManager = LinearLayoutManager(mainFragContext as MainActivity)
        // 6. 구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(mainFragViewOfLayout.mainRecyclerView.context, LinearLayoutManager(mainFragContext as MainActivity).orientation)
        mainFragViewOfLayout.mainRecyclerView.addItemDecoration(dividerItemDecoration)
        //
        //
        //*************************************************************//

        return mainFragViewOfLayout
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

    // 각각의 데이터 size 구하는 함수 (loadFlattenData 함수 선언 필수)
    fun dataSize(location: String) : Int {
        if (loadFlattenData(location) == null || loadFlattenData(location).size == null || loadFlattenData(location).size == 0) {
            return 0
        } else {
            return loadFlattenData(location).size
        }
    }

    // Flatten 데이터 로딩 함수 (loadTabData 함수 선언 필수)
    fun loadFlattenData(prefName: String) : MutableList<Memo> {

        // 쉐어드 가져오기
        val pref = (mainFragContext as MainActivity).getSharedPreferences(prefName, 0)

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

    // 데이터 정렬 함수
    fun dataSort(data: MutableList<Memo>) : MutableList<Memo> {
        mainFragViewOfLayout.mainRecyclerView.setHasFixedSize(true)

        data.sortBy { it.date_long }

        return data
    }

    // tab 데이터 로딩 함수
    fun loadTabData(prefName: String) : MutableList<String> {
        // 쉐어드 가져오기
        val pref = (mainFragContext as MainActivity).getSharedPreferences(prefName, 0)
        val dataString = pref.getString("tab", "")!! // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)

        var data = ArrayList<String>()

        // 데이터 값이 있다면
        if (dataString.isNotEmpty()) {
            // 저장된 문자열을 -> 객체 배열로 변경
            data = Gson().fromJson(dataString, Array<String>::class.java).toMutableList() as ArrayList<String>
        }

        return data
    }

    // 어댑터 갱신 함수
    fun refreshAdapter() {
        mainFragViewOfLayout.freezeCount.text = dataSize("freeze").toString()
        mainFragViewOfLayout.fridgeCount.text = dataSize("fridge").toString()
        mainFragViewOfLayout.roomCount.text = dataSize("room").toString()

        data = loadAllData()
        data = dataSort(data)
        adapter?.setData(data)
        adapter?.notifyDataSetChanged()
    }
}