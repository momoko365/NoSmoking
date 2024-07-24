package com.example.somke

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.somke.databinding.ActivityMainBinding
import com.example.somke.ui.DB.Data
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

//主にNavigation Componentを使ってアプリ内のナビゲーションとツールバー、ナビゲーションドロワーの設定を行うクラス
class MainActivity : AppCompatActivity() {
//ナビゲーションのトップレベルの目的地を設定するために使用
    private lateinit var appBarConfiguration: AppBarConfiguration
    //ActivityMainBindingクラスのインスタンスでレイアウトビューをバインディング
    private lateinit var binding: ActivityMainBinding

    private lateinit var db: DataBase
    private lateinit var dao: DataDao

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//レイアウトインフレ―多を使用してビューのバインディング
        binding = ActivityMainBinding.inflate(layoutInflater)
        //バインディングされたルートビューをコンテンツビューとして設定
        setContentView(binding.root)
//ツールバーをアクションバーとして設定
        setSupportActionBar(binding.appBarMain.toolbar)
        //ツールバーの文字の色の変更
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_color))

        //ドロワーレイアウトのインスタンス
        val drawerLayout: DrawerLayout = binding.drawerLayout
        //ナビゲーションビューのインスタンス
        val navView: NavigationView = binding.navView
        //ナビゲーションコントローラーのインスタンス。ナビゲーションの操作を管理
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // トップレベルの目的地とDrawerLayoutを設定
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_text_date, R.id.nav_record, R.id.nav_siori, R.id.nav_reset
            ), drawerLayout
        )
        // ActionBarとNavControllerの設定
        setupActionBarWithNavController(navController, appBarConfiguration)
        // NavigationViewとNavControllerの設定
        navView.setupWithNavController(navController)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    applicationContext,
                    DataBase::class.java,
                    "tabako.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.dataDAO()
            }

            val latestData = withContext(Dispatchers.IO) {
                dao.getLatestData()}
                withContext(Dispatchers.Main) {
                    updateNavigationView(latestData)
setupUpdateRunnable(latestData)
                }
            }


    }

    //ホーム画面のあなたの禁煙時間を計算するクラス
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTextViews(data:Data) {
        val dbDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val dbDate = dbDateFormat.parse(data.day)

        val dbLocalDateTime = LocalDateTime.ofInstant(dbDate.toInstant(), ZoneId.systemDefault())
        val currentLocalDateTime = getCurrentDateTime()

        val days = ChronoUnit.DAYS.between(dbLocalDateTime, currentLocalDateTime)
        val hours = ChronoUnit.HOURS.between(dbLocalDateTime, currentLocalDateTime) % 24
        val minutes = ChronoUnit.MINUTES.between(dbLocalDateTime, currentLocalDateTime) % 60
        val seconds = ChronoUnit.SECONDS.between(dbLocalDateTime, currentLocalDateTime) % 60

        val durationString = String.format("%02d日 %02d:%02d:%02d", days, hours, minutes, seconds)

        val kinenjikan = findViewById<TextView>(R.id.kinenjikan)
        val kinenhonsu = findViewById<TextView>(R.id.kinenhonsu)
        val kinenkingaku = findViewById<TextView>(R.id.kinenkingaku)
        val week = findViewById<TextView>(R.id.textView12)
        val month = findViewById<TextView>(R.id.textView13)
        val year = findViewById<TextView>(R.id.textView16)

        if (kinenjikan != null && kinenhonsu != null && kinenkingaku != null) {
            runOnUiThread {
                kinenjikan.text = "あなたの禁煙時間: $durationString"

                val secondsPerCigarette = 86400.0 / data.honsu
                val elapsedSeconds = ChronoUnit.SECONDS.between(dbLocalDateTime, currentLocalDateTime)
                val avoidedCigarettes = (elapsedSeconds / secondsPerCigarette).toInt()
                kinenhonsu.text = "禁煙した本数: $avoidedCigarettes 本"

                val pricePerCigarette = data.money /20
                val savedAmount = avoidedCigarettes * pricePerCigarette
                kinenkingaku.text = "節約した金額: ¥$savedAmount"

                val weekPrice = data.money / 20 * data.honsu * 7
                val monthPrice = data.money / 20 * data.honsu * 31
              val yearPrice =   data.money / 20 * data.honsu * 365
                week.text = "1週間で節約できるお金: ￥$weekPrice"
                month.text = "1か月で節約できるお金: ￥$monthPrice"
                year.text = "1年で節約できるお金: ￥$yearPrice"
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // メニューをインフレートする（ActionBarにアイテムを追加する）
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //ハンバーガーメニューの禁煙開始日の日付レイアウト変更画面を作るクラス
    private fun updateNavigationView(data: Data?) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        val navTextDateItem = menu.findItem(R.id.nav_text_date)

        data?.let {
            // フォーマット
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val date = dateFormat.parse(it.day)

            // フォーマットするためのフォーマッターを作成
            val dateFormatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.ENGLISH)
            val formattedDate = dateFormatter.format(date)

            // UIを更新するためにメインスレッドで実行する
            runOnUiThread {
                navTextDateItem.title = "禁煙開始日： $formattedDate"
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        // NavControllerでのNavigateUp処理を行う
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUpdateRunnable(data: Data) {
        updateRunnable = object : Runnable {
            override fun run() {
                updateTextViews(data)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable)
    }

}