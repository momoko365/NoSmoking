package com.example.somke.ui.reset

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.somke.databinding.FragmentResetBinding
import com.example.somke.ui.DB.DataBase
import com.example.somke.ui.DB.DataDao
import com.example.somke.ui.Start_screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SlideshowFragment : Fragment() {

    private var _binding: FragmentResetBinding? = null

    private val binding get() = _binding!!
    private lateinit var db: DataBase
    private lateinit var dao: DataDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentResetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView = binding.reset
        val button = binding.resetBtn

        button.setBackgroundColor(Color.RED)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                db = Room.databaseBuilder(
                    requireContext(),
                    DataBase::class.java,
                    "tabako.db"
                ).fallbackToDestructiveMigration().build()
                dao = db.dataDAO()
            }
        }


        button.setOnClickListener {
            // リセットボタンがクリックされたときの処理
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteAll()
            }
            val intent = Intent(requireContext(), Start_screen::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}