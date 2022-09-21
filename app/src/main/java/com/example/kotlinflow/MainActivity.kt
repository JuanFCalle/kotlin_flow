package com.example.kotlinflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlinflow.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.setOnClickListener {
            viewModel.accept(UiAction.Search("New Query 1"))
        }
        binding.btn2.setOnClickListener {
            viewModel.accept(UiAction.Scroll("default_query"))
        }
        binding.btn3.setOnClickListener {
            viewModel.accept(UiAction.Scroll("New Query 1"))
        }
        binding.btn4.setOnClickListener {
//            viewModel.accept(UiAction.Btn2("action Btn2"))
        }
        subscribeToObservable()
    }

    private fun subscribeToObservable() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state1.collect {
                        binding.multiText.text =
                            "Query: ${it.query}\nLastQueryScrolled: ${it.lastQueryScrolled}"
                    }
                }
                launch {
                    viewModel.state2.collect {
//                        binding.multiText.text = "${binding.multiText.text} ${it}"
                    }
                }
            }
        }
    }
}