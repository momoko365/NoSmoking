package com.example.somke.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.somke.R
import com.example.somke.databinding.FragmentRecordBinding
import com.example.somke.ui.DB.Data
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import com.example.somke.ui.reset.SlideshowViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordFragment : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DataBase
    private lateinit var dao: DataDao
    private var latestData: Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                    binding.reHonsu.setText(data.honsu.toString())
                    binding.reNedan.setText(data.money.toString())
                }
            }
        }

        // EditTextをbindingを使って取得する
        val honsu: EditText = binding.reHonsu
        val nedan: EditText = binding.reNedan
        val btn: ImageButton = binding.torokubtn

        // 更新ボタンのクリックリスナー
        btn.setOnClickListener {
            val newHonsu = honsu.text.toString().toIntOrNull()
            val newNedan = nedan.text.toString().toIntOrNull()

            if (newHonsu != null && newNedan != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    latestData?.let { data ->
                        val updatedData = data.copy(honsu = newHonsu, money = newNedan)
                        dao.updatetabako(honsu = newHonsu, money = newNedan)
                        withContext(Dispatchers.Main) {
                            // エディットテキストをクリア
                            honsu.text.clear()
                            nedan.text.clear()
                            Toast.makeText(requireContext(), "更新完了", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // エラー処理（例えば、Toastを表示）
                Toast.makeText(requireContext(), "入力してください", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
