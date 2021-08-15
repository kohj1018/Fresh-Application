package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_view_pager.view.*

class CustomViewPagerAdapter(val context: Context, val location: String, val tabData: MutableList<String>) : RecyclerView.Adapter<CustomViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setMemoList(data: MutableList<Memo>) {
            val adapter = CustomAdapter(itemView.context, false)
            adapter.listData = dataSort(data)
            itemView.pager_recycler.adapter = adapter
            itemView.pager_recycler.layoutManager = LinearLayoutManager(itemView.context)
        }

        // 데이터 정렬 함수
        fun dataSort(data: MutableList<Memo>) : MutableList<Memo> {
            itemView.pager_recycler.setHasFixedSize(true)

            data.sortBy { it.date_long }

            return data
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewPagerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tabData.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val pref = context.getSharedPreferences(location, 0)
        val dataString = pref.getString(tabData[position], "")!! // 1번째 인자 키 값, 2번째 인자 해당안에 비어있을 경우 디폴트 값(느낌표 두개는 String? 안되게 String임을 강요하는 것)

        var data = ArrayList<Memo>()

        // 데이터 값이 있다면
        if (dataString.isNotEmpty()) {
            // 저장된 문자열을 -> 객체 배열로 변경
            data = Gson().fromJson(dataString, Array<Memo>::class.java).toMutableList() as ArrayList<Memo>
        }

        holder.setMemoList(data)
    }
}