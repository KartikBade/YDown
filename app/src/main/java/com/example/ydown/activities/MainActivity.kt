package com.example.ydown.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.ydown.adapters.QualityListAdapter
import com.example.ydown.databinding.ActivityMainBinding
import com.example.ydown.repositories.PythonRepository
import com.example.ydown.viewmodels.MainViewModel
import com.example.ydown.viewmodels.MainViewModelProviderFactory
import com.example.ydown.workmanager.DownloadWorker
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

        val action = intent.action
        val type = intent.type
        if ("android.intent.action.SEND" == action && type != null && "text/plain" == type) {
            Log.e("MainActivity", "${ intent.getStringExtra("android.intent.extra.TEXT") }")
            intent.getStringExtra("android.intent.extra.TEXT")?.let {
                mainViewModel.link = it
                mainViewModel.getQualityList()
            }
        }

        val workManager = WorkManager.getInstance(applicationContext)
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
        val data = Data.Builder()

        val destination = getSharedPreferences("DestinationSharedPref", MODE_PRIVATE)
            .getString("destination", "/storage/emulated/0/Download/YDown")
        val qualityListAdapter = QualityListAdapter {
            data.putString("link", mainViewModel.link)
            data.putInt("position", it)
            data.putString("destination", destination)
            workRequest.setInputData(data.build())
            workManager.enqueue(workRequest.build())
        }
        binding.mainQualityRv.adapter = qualityListAdapter
        mainViewModel.qualityList.observe(this) {
            if (it.isEmpty()) { binding.ivEmptyMainQualityRv.visibility = View.VISIBLE }
            else { binding.ivEmptyMainQualityRv.visibility = View.GONE }
            qualityListAdapter.submitList(it)
        }

        binding.ivSearch.setOnClickListener {
            val linkString = binding.etLink.text.toString().trim()
            if (linkString.isBlank() || linkString == "null") { return@setOnClickListener }
            else { mainViewModel.link = linkString }
            binding.etLink.text.clear()
            if (mainViewModel.link.isBlank() || mainViewModel.link == "null") { return@setOnClickListener }
            mainViewModel.getQualityList()
        }
    }

    private fun setDestination(destination: String) {
        val sharedPreferences = getSharedPreferences("DestinationSharedPref", MODE_PRIVATE)
        sharedPreferences.edit().putString("destination", destination).apply()
    }
}