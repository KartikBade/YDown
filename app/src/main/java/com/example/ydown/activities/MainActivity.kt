package com.example.ydown.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.ydown.adapters.QualityListAdapter
import com.example.ydown.databinding.ActivityMainBinding
import com.example.ydown.repositories.PythonRepository
import com.example.ydown.viewmodels.MainViewModel
import com.example.ydown.viewmodels.MainViewModelProviderFactory
import com.example.ydown.viewmodels.Status
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
        val link = intent.getStringExtra("android.intent.extra.TEXT")
        if ("android.intent.action.SEND" == action && type != null && "text/plain" == type && link != null) {
            mainViewModel.checkForIntents(link)
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

        mainViewModel.status.observe(this) {
            when (it) {
                Status.LOADING -> {
                    binding.ivEmptyMainQualityRv.setImageResource(com.example.ydown.R.drawable.loading_animation)
                    binding.ivEmptyMainQualityRv.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.ivEmptyMainQualityRv.setImageResource(com.example.ydown.R.drawable.y_down_empty_main_list)
                    binding.ivEmptyMainQualityRv.visibility = View.VISIBLE
                    Toast.makeText(this, "Unknown Error Occurred!", Toast.LENGTH_SHORT).show()
                }
                Status.DONE -> {
                    if (mainViewModel.qualityList.value?.isEmpty() == false) {
                        binding.ivEmptyMainQualityRv.visibility = View.GONE
                    }
                    else {
                        binding.ivEmptyMainQualityRv.setImageResource(com.example.ydown.R.drawable.y_down_empty_main_list)
                        binding.ivEmptyMainQualityRv.visibility = View.VISIBLE
                        Toast.makeText(this, "No downloads available.", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    binding.ivEmptyMainQualityRv.setImageResource(com.example.ydown.R.drawable.y_down_empty_main_list)
                    binding.ivEmptyMainQualityRv.visibility = View.VISIBLE
                }
            }
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