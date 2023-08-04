package com.example.ydown.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.ydown.adapters.QualityListAdapter
import com.example.ydown.databinding.ActivityMainBinding
import com.example.ydown.repositories.PythonRepository
import com.example.ydown.viewmodels.MainViewModel
import com.example.ydown.viewmodels.MainViewModelProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pythonRepository: PythonRepository
    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainViewModelProviderFactory = MainViewModelProviderFactory(pythonRepository)
        mainViewModel = ViewModelProvider(this, mainViewModelProviderFactory)[MainViewModel::class.java]

        val qualityListAdapter = QualityListAdapter {
            mainViewModel.downloadVideo(it)
        }
        binding.mainQualityRv.adapter = qualityListAdapter
        mainViewModel.qualityList.observe(this) {
            if (it.isEmpty()) { binding.ivEmptyMainQualityRv.visibility = View.VISIBLE }
            else { binding.ivEmptyMainQualityRv.visibility = View.GONE }
            qualityListAdapter.submitList(it)
        }

        binding.ivSearch.setOnClickListener {
            val link = binding.etLink.text.toString()
            if (link.isEmpty() || link.isBlank() || link == "null") { return@setOnClickListener }
            mainViewModel.getQualityList()
        }
    }
}