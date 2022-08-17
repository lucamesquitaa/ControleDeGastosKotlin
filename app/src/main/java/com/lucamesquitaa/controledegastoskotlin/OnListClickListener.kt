package com.lucamesquitaa.controledegastoskotlin

import com.lucamesquitaa.controledegastoskotlin.model.Item


interface OnListClickListener {
    fun onLongClick(position: Int, item: Item)
}
