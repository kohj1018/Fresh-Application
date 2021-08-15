package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.recipe_item.view.*


class RecipeAdapter(val context: Context): RecyclerView.Adapter<RecipeAdapter.Holder>() {

    var recipeItems = mutableListOf<SimpleRecipe>()

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, RecipeDetailActivity::class.java)
                intent.putExtra("dataIdx", recipeItems[adapterPosition].dataIdx)
                itemView.context.startActivity(intent)
            }
        }

        fun setItem(simpleRecipe: SimpleRecipe) {
            loadImageViaUrl(itemView.thumbnail, simpleRecipe.thumbnail)

            itemView.title.text = simpleRecipe.title
        }

        private fun loadImageViaUrl(view: ImageView, url: String) {
            val options = RequestOptions()
                .placeholder(R.drawable.ic_init)

            Glide.with(view.context)
                .setDefaultRequestOptions(options)
                .load(url)
                .into(view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // 특정 xml 파일을 클래스로 변환
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)

        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return recipeItems.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val simpleRecipe = recipeItems[position]
        holder.setItem(simpleRecipe)
    }
}