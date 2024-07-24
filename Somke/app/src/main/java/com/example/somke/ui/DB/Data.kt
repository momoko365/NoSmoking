package com.example.somke.ui.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity
data class Data(
    @PrimaryKey(autoGenerate = true)
    var id: Int, val day: String, val time: String ,val honsu: Int , val money: Int) {

    data class Message(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        val message: Int
    )

}