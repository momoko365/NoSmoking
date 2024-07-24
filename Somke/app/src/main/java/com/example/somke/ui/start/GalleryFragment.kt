package com.example.somke.ui.start
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.somke.databinding.FragmentStartBinding
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var db: DataBase
    private lateinit var dao: DataDao
    private var latestData: com.example.somke.ui.DB.Data? = null
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        val startDate = binding.startDate
        val startTime = binding.startTime
        val btn: ImageButton = binding.button2

        startDate.setBackgroundColor(Color.DKGRAY)
        startTime.setBackgroundColor(Color.DKGRAY)

        // 初期表示のフォーマット
        startDate.text = updateDateDisplay().toString()
        startTime.text=updateTimeDisplay().toString()

        startDate.setOnClickListener {
            showDatePickerDialog()
        }

        startTime.setOnClickListener {
            showTimePickerDialog()
        }

        // データベース初期化
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    requireContext(),
                    DataBase::class.java,
                    "tabako.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.dataDAO()
            }

            latestData = withContext(Dispatchers.IO) {
                dao.getLatestData()
            }

            withContext(Dispatchers.Main) {
                // データが存在する場合のみ更新処理を実行
                latestData?.let { data ->
                    val dateTimeFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPANESE)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

                    val parsedDate = dateTimeFormat.parse(data.day)

                    startDate.text = dateFormat.format(parsedDate)
                    startTime.text = timeFormat.format(parsedDate)
                }
            }
        }

        btn.setOnClickListener {
            registerDateTime()
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            updateDateDisplay()
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            updateTimeDisplay()
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.JAPANESE)
        binding.startDate.text = dateFormat.format(calendar.time)
    }

    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        binding.startTime.text = timeFormat.format(calendar.time)
    }

    private fun registerDateTime() {
        val dateTimeFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val dateTimeString = dateTimeFormat.format(calendar.time)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dao.updatetime(day = dateTimeString, time = dateTimeString)
            }
        }
    }
}