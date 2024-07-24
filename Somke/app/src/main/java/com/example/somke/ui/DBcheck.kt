package com.example.somke.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.somke.MainActivity
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DBcheck: AppCompatActivity() {
    private lateinit var db: DataBase
    private lateinit var dao: DataDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    applicationContext,
                    DataBase::class.java,
                    "tabako.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.dataDAO()
            }

            // データベースの中身をチェック
            val latestData = dao.getLatestData()
            withContext(Dispatchers.Main) {
                if (latestData != null) {
                    // データが存在する場合、MainActivityに遷移
                    val intent = Intent(this@DBcheck, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    // データが存在しない場合、MainActivityに遷移
                    val intent = Intent(this@DBcheck, Start_screen::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }
}