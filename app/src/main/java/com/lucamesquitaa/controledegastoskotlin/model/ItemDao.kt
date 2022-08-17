package com.lucamesquitaa.controledegastoskotlin.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    fun insert(item: Item)

    @Query("SELECT * FROM Item")
    fun getAllItems(): List<Item>

    @Delete
    fun delete(item: Item): Int // FIXME: retorna 1 se deu sucesso

}