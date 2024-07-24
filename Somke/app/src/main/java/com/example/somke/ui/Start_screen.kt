package com.example.somke.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.somke.MainActivity
import com.example.somke.R
import com.example.somke.databinding.StartScreenBinding
import com.example.somke.ui.DB.Data
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Start_screen : AppCompatActivity(){

    private var _binding: StartScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DataBase
    private lateinit var dao: DataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//StartScreenBindingを初期化してレイアウトをセット
        _binding = StartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ツールバーを非表示にする
        supportActionBar?.hide()

//データベースの初期化
      GlobalScope.launch {
          withContext(Dispatchers.IO){
              db = Room.databaseBuilder(
                  applicationContext,
                  DataBase::class.java,
                  "tabako.db"
              ).fallbackToDestructiveMigration().build()
              dao=db.dataDAO()
          }
      }

        //UI要素の取得
        var honsu =findViewById<EditText>(R.id.textView10)
        var nedan =findViewById<EditText>(R.id.textView11)
        val button = findViewById<ImageButton>(R.id.button)

//ボタンの操作
        button.setOnClickListener {
                    // ボタンが離された時に行う処理をここに移動する
                    val honsuValue = honsu.text.toString().toIntOrNull()
                    val nedanValue = nedan.text.toString().toIntOrNull()


            if (honsuValue != null && nedanValue != null){
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Tokyo")
                }
                val timeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Tokyo")
                }
                val date = dateFormat.parse(dateFormat.format(currentDate))
                val time = timeFormat.parse(timeFormat.format(currentDate))

                GlobalScope.launch(Dispatchers.IO) {
                    val data = Data(honsu = honsuValue, money = nedanValue, day = date.toString(), time = time.toString(), id = 0)
                    dao.insert(data)
                }
                // Intentを使用してMainActivityに遷移
                val intent = Intent(this@Start_screen, MainActivity::class.java)
                startActivity(intent)
                // finish()はここでは不要

            }else{
                // エラー処理（例えば、Toastを表示）
                Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}