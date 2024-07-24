package com.example.somke.ui.siori

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.somke.databinding.FragmentSioriBinding

class SioriFragment : Fragment() {
    private var _binding: FragmentSioriBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSioriBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TextViewのスクロール関連設定
        val hogeTextView: TextView = binding.textView18
        hogeTextView.isVerticalScrollBarEnabled = true
        hogeTextView.movementMethod = ScrollingMovementMethod()

        hogeTextView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            // スクロールが変更されたときの処理
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}