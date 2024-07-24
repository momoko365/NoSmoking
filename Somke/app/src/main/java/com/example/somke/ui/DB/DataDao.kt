package com.example.somke.ui.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {

    @Insert
    fun insert(data: Data)
    @Query("select * from data")
    fun getAll():LiveData<List<Data>>

    @Query("UPDATE data SET honsu = :honsu, money = :money ")
    fun updatetabako(honsu: Int, money: Int)

    @Query("UPDATE data SET time = :time, day = :day ")
    fun updatetime( day: String, time: String)

    @Query("DELETE FROM data")
    fun deleteAll()

    @Query("select * from data ORDER BY id DESC LIMIT 1")
    suspend fun getLatestData():Data


//    fun getLatestData(): LiveData<Data>

}

