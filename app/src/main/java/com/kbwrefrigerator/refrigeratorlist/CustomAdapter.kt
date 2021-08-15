package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.item.view.textDate
import kotlinx.android.synthetic.main.item.view.textDday
import kotlinx.android.synthetic.main.item.view.textName
import kotlinx.android.synthetic.main.item_edit.view.*
import java.util.*
import kotlinx.android.synthetic.main.item.view.textCategory
import kotlinx.android.synthetic.main.item.view.textLoc

val ONE_DAY = (24 * 60 * 60 * 1000)

// RecyclerView.Adapter의 상속을 받아 커스텀어댑터를 만든다.
// 상속을 받을 때 반드시 오버라이드 해줘야 하는 함수가 있다 밑의 3개 함수가 바로 그것.
// editmode 인자는 true일 경우 delete mode 인 상황이다.(activity_main_edit.xml로 넘어간 상황)
class CustomAdapter(val context: Context, var editmode: Boolean)
    : RecyclerView.Adapter<CustomAdapter.Holder>(), Filterable {    // Filterable은 search 용

    var listData = mutableListOf<Memo>()
    var listDataFilter = mutableListOf<Memo>()

    // search 기능 이용시 사용하는 함수. listDataFilter에도 데이터를 한번에 넣어주기 위해 편의상 만든 거
    fun setData(listData: MutableList<Memo>) {
        this.listData = listData
        this.listDataFilter = listData
    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        // 홀더의 아이템을 클릭할 경우 창 넘어가기 기능
        init {
            if (editmode == false) {
                itemView.setOnClickListener {
                    val detailIntent = Intent(itemView.context, DetailActivity::class.java)
                    detailIntent.putExtra("name", itemView.textName.text)
                    detailIntent.putExtra("date", itemView.textDate.text)
                    detailIntent.putExtra("Dday", itemView.textDday.text)
                    detailIntent.putExtra("date_long", ( getIgnoredTimeDays(listData[adapterPosition].date_long) - (ONE_DAY*(24-15)/24) ) ) // 사용자가 설정한 날짜의 하루 더가서 0시 기준으로 long Type의 시간을 보냄
                    detailIntent.putExtra("loc", itemView.textLoc.text)
                    detailIntent.putExtra("category", itemView.textCategory.text)
                    detailIntent.putExtra("uriString", listData[adapterPosition].uriString)  // 수정할 때 수정할 아이템의 사진에 접근하기 위해 보내는 값
                    itemView.context.startActivities(arrayOf(detailIntent))
                }
                itemView.setOnLongClickListener {
                    // MainActivity_edit.kt 에서 사용하기 위해 잠시 저장
                    saveDataOfAdapter(context, listData)

                    val editIntent = Intent(context, MainActivity_edit::class.java)
                    editIntent.putExtra("position", adapterPosition)
                    context.startActivities(arrayOf(editIntent))
                    true
                }
            } else {        // (editmode == true)
                itemView.setOnClickListener {
                    if (itemView.checkBox.isChecked == false){
                        itemView.checkBox.isChecked = true
                        listData[adapterPosition].check = true
                    } else {
                        itemView.checkBox.isChecked = false
                        listData[adapterPosition].check = false
                    }
                }
            }
        }

        fun setMemo(memo: Memo) {
            itemView.textName.text = memo.name
            itemView.textDate.text = memo.date
            itemView.textCategory.text = memo.category
            if (editmode == false){
                Glide.with(itemView)
                    .load(memo.uriString)
                    .placeholder(R.drawable.ic_init)
                    .error(R.drawable.ic_init)
                    .into(itemView.imageItem)
            }
            when (memo.loc) {
                0 -> {
                    itemView.textLoc.text = "냉동"
                }
                1 -> {
                    itemView.textLoc.text = "냉장"
                }
                2 -> {
                    itemView.textLoc.text = "실온"
                }
            }

            // 두 날짜를 Long Type으로 뺴고 ONE_DAY(= 24 * 60 * 60 * 1000) 나눠서 며칠 차인지 구함
            var dday = ( getIgnoredTimeDays(memo.date_long) - getIgnoredTimeDays(System.currentTimeMillis()) ) / ONE_DAY
            if (dday == 0L) {
                itemView.textDday.text = "D - DAY"
                itemView.textDday.setTextSize(20.toFloat())
                itemView.textDday.setTextColor(Color.parseColor("#FF2727")) // 더 빨간색(백업 #FF0000)
            } else if (dday > 0L) {
                itemView.textDday.text = "D - ${dday}"
                if (dday <= 3L) {
                    itemView.textDday.setTextColor(Color.parseColor("#FF2727")) // 빨간색(백업 #E84646)
                } else if (dday <= 7L) {
                    itemView.textDday.setTextColor(Color.parseColor("#FF8B00")) // 노란색(약간 겨자색)  (백업 #D8B713)
                } else {
                    itemView.textDday.setTextColor(Color.parseColor("#1BB222")) // 연두색(백업 #7CB639)
                }
            } else {
                itemView.textDday.text = "D + ${( getIgnoredTimeDays(System.currentTimeMillis()) - getIgnoredTimeDays(memo.date_long) ) / ONE_DAY}" // 날짜가 지날땐 다시계산
                itemView.textDday.setTextColor(Color.parseColor("#000000")) // 검은색
            }

            // editmode에서 memo의 check가 true면 itemView의 checkBox를 true인 상태로 내보냄(activity_main에서 선택한 뷰홀더를 체크박스 true인 상태로 보내기 위한 장치)
            if (editmode == true && memo.check == true) {
                itemView.checkBox.isChecked = true
            } else if (editmode == true && memo.check == false) {   /*** 오류 해결 코드!! ***/ // 긴 목록에서 스크롤을 밑으로 내렸다 올렸다 하면 체크하지 않았던 항목이 체크가 되는 버그가 있었다.
                itemView.checkBox.isChecked = false                                        // 그것은 바로 위의 if문 코드 때문이었는데 이 else if문을 추가함으로써 문제가 해결되었다.
            }
        }

        // Long Type으로 된 날짜값을 받아서 해당 날짜의 연,월,일만 남기고 시간 같은 건 초기화하는 함수
        fun getIgnoredTimeDays(time: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = time

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        // 데이터 저장 함수
        fun saveDataOfAdapter(context: Context, data: MutableList<Memo>) {
            // 매개변수로 들어온 배열을 -> 문자열로 변환
            val dataListString = Gson().toJson(data)

            // 쉐어드 가져오기
            val pref = context.getSharedPreferences("temporary", 0)     // MainActivity_edit.kt 에서 사용하기 위해 잠시 사용하는 저장소
            val editor = pref.edit() // 수정모드

            editor.putString("data", dataListString) // 1번째 인자 키 값, 2번째 인자 실제 담아둘 값
            editor.apply()
        }
    }

    /********************************** 안드로이드 시스템이 스스로 실행하는 함수들 **********************************/
    // 뷰홀더를 만드는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        if (editmode == true) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)
            return Holder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
            return Holder(itemView)
        }
    }

    // 안드로이드에 데이터의 사이즈를 리턴해주는 함수
    override fun getItemCount(): Int {
        return listData.size
    }

    // 뷰홀더에 값을 설정해주는 함수
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = listData[position]
        holder.setMemo(memo)

//        // 간격 설정
//        val layoutParams = holder.itemView.layoutParams
//        layoutParams.height = 250
//        holder.itemView.requestLayout()
    }

    // Filter 꺼(검색용)
    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (p0 == null || p0.length < 0) {
                    filterResults.count = listDataFilter.size
                    filterResults.values = listDataFilter
                } else {
                    var searchChar = p0.toString().toLowerCase()

                    var resultListData = mutableListOf<Memo>()

                    for (item in listDataFilter) {
                        if (item.name!!.contains(searchChar)) {
                            resultListData.add(item)
                        }
                    }

                    filterResults.count = resultListData.size
                    filterResults.values = resultListData
                }

                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                listData = p1!!.values as MutableList<Memo>
                notifyDataSetChanged()
            }

        }
    }
    /******************************************************************************************************/
}