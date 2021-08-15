package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipe.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecipeFragment : Fragment() {
    // 리사이클러뷰에 들어갈 데이터
    var recipeData: MutableList<SimpleRecipe> = mutableListOf()

    // 시작하는 인덱스 0으로 초기화
    var startIdx = 0

    // 오류 보완 장치
    var isFetching = false

    // fragment에서 사용할 Context
    private lateinit var recipeFragContext: Context

    // fragment에서 사용할 View
    private lateinit var recipeFragViewOfLayout: View

    // fragment에서 사용할 context를 얻는 과정(기본적으로 fragment는 Activity가 아니기때문에 context가 없다.)
    // onAttach lifeCycle에서 얻는 것이 getActivity를 이용한 방법보다 안전한 방법이다.(context가 가끔 null 이 되기도 한다.)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        recipeFragContext = context    // Root Activity에 접근할 경우 context as Activity 를 이용
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recipeFragViewOfLayout = inflater.inflate(R.layout.fragment_recipe, container, false)

        // toolbar
        var toolbar = recipeFragViewOfLayout.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (recipeFragContext as MainActivity).setSupportActionBar(toolbar)  // mainFragContext context로 MainActivity를 불러와 사용
        var actionBar = (recipeFragContext as MainActivity).supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌

        //*************************************************************//
        //
        // 1. 데이터 로딩
        // 2. 어댑터 생성
        var adapter = RecipeAdapter(recipeFragContext)
        // 3. 어댑터에 데이터 전달
        adapter.recipeItems = recipeData
        // 4. 화면에 있는 리사이클러뷰에 어댑터 연결
        recipeFragViewOfLayout.recipeRecyclerView.adapter = adapter
        // 5. 레이아웃 매니저 연결(GridLayoutManager를 이용하고 spanCount에 3을 대입해서 한줄에 3개씩 들어가도록 함)
        recipeFragViewOfLayout.recipeRecyclerView.layoutManager = GridLayoutManager(recipeFragContext as MainActivity, 3)
        // 6. 리사이클러뷰 스크롤시 아이템 갱신
        recipeFragViewOfLayout.recipeRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 바닥을 찍을 때
                if (!recipeFragViewOfLayout.recipeRecyclerView.canScrollVertically(1) && !isFetching) {
                    Toast.makeText(recipeFragContext, "로딩중..", Toast.LENGTH_SHORT).show()

                    isFetching = true
                    getRecipeData( (startIdx+1).toString(), (startIdx+60).toString() ) {
                        adapter.notifyDataSetChanged()
                        startIdx += 60
                        isFetching = false
                    }

                }
            }

        })

        Toast.makeText(recipeFragContext, "로딩중..", Toast.LENGTH_SHORT).show()

        // 7. 데이터를 다 받아오면 업데이트해서 보여주기
        isFetching = true
        getRecipeData((startIdx+1).toString(), (startIdx+60).toString()) {
            adapter.notifyDataSetChanged()
            startIdx += 60
            isFetching = false
        }
        //
        //
        //*************************************************************//

        return recipeFragViewOfLayout
    }

    // retrofit 이용
    private fun getRecipeData(startIdx: String, endIdx: String, callback: () -> Unit) {
        /**
         * retrofit 객체생성
         * 서버에서 데이터를 JSON으로 돌려주기 때문에 GsonConverterFactory.create()를 통하여 JSON을 변환해주는 컨버터도 추가해주고 build()를 통해 생성
         */
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.foodsafetykorea.go.kr/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /**
         * retrofit 객체를 통해 인터페이스 생성
         * 인터페이스에 정의된 API 엔드포인트들의 구현체를 만든다
         */
        val service = retrofit.create(RecipeApiService::class.java)

        service.getSimpleData("d2495c39a59f4a9e9715", "json", startIdx, endIdx).enqueue(object: Callback<RecipeApiSimpleResponse> {

            override fun onResponse(
                call: Call<RecipeApiSimpleResponse>,
                response: Response<RecipeApiSimpleResponse>
            ) {
                if (response.isSuccessful) {
                    // RecipeApiService 인터페이스 안의 값에 따라, 오픈 api를 통해 온 response의 바디부분에서 data - rows로 들어간다음 거기서 for을 돌리며 아이템 요소와 인덱스를 받아옴
                    for ((index, element) in response.body()?.data?.rows!!.withIndex()) {
                        // SimpleRecipe 이름의 data class의 값들을 넣음
                        val simpleRecipe = SimpleRecipe(element.thumbnailUrl, element.title, (Integer.parseInt(startIdx) + index))
                        recipeData.add(simpleRecipe)
                    }
                    // 위에 getData 파라미터에 있음.
                    callback()
                }
            }

            // 실패했을 때
            override fun onFailure(call: Call<RecipeApiSimpleResponse>, t: Throwable) {
                Log.e("Response Error ::", "Failed API call with call: " + call +
                        " + exception: " + t)
            }

        })

    }
}