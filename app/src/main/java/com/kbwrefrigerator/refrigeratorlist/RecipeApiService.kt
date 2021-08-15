package com.kbwrefrigerator.refrigeratorlist

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeApiService {
    @GET("{keyId}/COOKRCP01/{dataType}/{startIdx}/{endIdx}")
    fun getSimpleData(@Path("keyId") apiKey: String,
                      @Path("dataType") dataType: String = "json",
                      @Path("startIdx") startIdx: String = "0",
                      @Path("endIdx") endIdx: String = "10"): Call<RecipeApiSimpleResponse>

    @GET("{keyId}/COOKRCP01/{dataType}/{startIdx}/{endIdx}")
    fun getDetailData(@Path("keyId") apiKey: String,
                      @Path("dataType") dataType: String = "json",
                      @Path("startIdx") startIdx: String = "0",
                      @Path("endIdx") endIdx: String = "10"): Call<RecipeApiDetailResponse>
}

// 응답 받을 데이터 구조와 같은 구조여야하고 변수명도 같아야한다.
data class RecipeApiSimpleResponse(
    @SerializedName("COOKRCP01") val data: SimpleRows
)

data class SimpleRows(
    @SerializedName("row") val rows: Array<Row>
)

data class Row(
    @SerializedName("RCP_NM") val title: String,
    @SerializedName("ATT_FILE_NO_MAIN") val thumbnailUrl: String
)

data class RecipeApiDetailResponse(
    @SerializedName("COOKRCP01") val data: DetailRows
)

data class DetailRows(
    @SerializedName("row") val rows: Array<Map<String, String>>
)