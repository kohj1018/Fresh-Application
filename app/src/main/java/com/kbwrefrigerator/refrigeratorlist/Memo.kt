package com.kbwrefrigerator.refrigeratorlist

data class Memo(var name: String?, var date: String?, var date_long: Long, var loc: Int, var category: String, var check: Boolean, var uriString: String)

data class SimpleRecipe(val thumbnail: String, val title: String, val dataIdx: Int)
data class DetailRecipe(val thumbnail: String, val title: String, val kind: String, val calorie: String, val ingredients: String, val descText: List<Pair<Int, String>>, val descImg: List<Pair<Int, String>>)