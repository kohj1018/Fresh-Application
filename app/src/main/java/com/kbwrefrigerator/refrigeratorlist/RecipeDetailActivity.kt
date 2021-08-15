package com.kbwrefrigerator.refrigeratorlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeDetailActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_recipe_detail)

        recipeDetailProgressBar.isVisible = true

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)


        var index = intent.getIntExtra("dataIdx", 0)

        getDetailRecipeData("${index}") {
            recipeDetailTitle.text = it.title
            recipeDetailKind.text = it.kind
            recipeDetailCalorie.text = it.calorie
            Glide.with(this).load(it.thumbnail).into(recipeDetailThumbnail)
            recipeDetailIngredients.text = it.ingredients

            for (i in it.descText) {
                recipeDetailText.append("${i.second} \n\n\n")
            }

            recipeDetailProgressBar.isVisible = false
        }
    }

    private fun getDetailRecipeData(index: String, callback: (DetailRecipe) -> Unit) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://openapi.foodsafetykorea.go.kr/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(RecipeApiService::class.java)

            service.getDetailData("d2495c39a59f4a9e9715", "json", index, index).enqueue(object: Callback<RecipeApiDetailResponse> {
                override fun onResponse(
                    call: Call<RecipeApiDetailResponse>,
                    response: Response<RecipeApiDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        for (element in response.body()?.data?.rows!!) {
                            val descText = mutableListOf<Pair<Int, String>>()
                            val descImg = mutableListOf<Pair<Int, String>>()
                            var title = ""
                            var thumbnail = ""
                            var kind = ""
                            var calorie = ""
                            var ingredients = ""

                            for ((key, value) in element) {
                                // 데이터 분류
                                if (value.isNotEmpty()) {
                                    if (key.contains("MANUAL_IMG")) {
                                        descImg.add(Integer.parseInt(key.split("MANUAL_IMG")[1]) to value)
                                    } else if (key.contains("MANUAL")) {
                                        descText.add(Integer.parseInt(key.split("MANUAL")[1]) to value)
                                    } else if (key.contains("ATT_FILE_NO_MK")) {
                                        thumbnail = value
                                    } else if (key.contains("RCP_NM")) {
                                        title = value
                                    } else if (key.contains("RCP_PARTS_DTLS")) {
                                        ingredients = value
                                    } else if (key.contains("RCP_PAT2")) {
                                        kind = "#$value"
                                    } else if (key.contains("INFO_ENG")) {
                                        calorie = value + "kcal"
                                    }
                                }
                            }

                            // 데이터 순서 정렬
                            descText.sortBy { it.first }
                            descImg.sortBy { it.first }

                            callback(DetailRecipe(thumbnail, title, kind, calorie, ingredients, descText, descImg))
                        }

                    }

                }

                override fun onFailure(call: Call<RecipeApiDetailResponse>, t: Throwable) {
                    Log.d("Response Error ::", "Failed API call with call: " + call +
                            " + exception: " + t)
                }
            })
        } catch (e: Exception) {
            throw e
        }
    }
}