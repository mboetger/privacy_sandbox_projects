package com.example.privacy_ads_consumer

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import androidx.privacysandbox.ads.adservices.topics.GetTopicsRequest
import androidx.privacysandbox.ads.adservices.topics.TopicsManager
import com.example.privacy_ads_consumer.ui.theme.Privacy_ads_consumerTheme
import kotlinx.coroutines.launch

private const val TAG = "privacy_ads_consumer"

class MainActivity : ComponentActivity() {
    private var topicsManager: TopicsManager? = null
    private lateinit var topicsRequestBuilder:  GetTopicsRequest.Builder
    // State variable to hold the topics string for the UI
    private var topicsResultText by mutableStateOf("") // Initialize with default text

    private fun initTopics() {
        if (SDK_INT >= Build.VERSION_CODES.R && // The extensions API is available since R.
            SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4
        ) {
            // The initialization function will return null if the requested
            // functionality is not available on the device.
            topicsManager = TopicsManager.obtain(baseContext)
            if (topicsManager == null) {
                topicsResultText = "Could not get topics manager"
                Log.e(TAG, "Could not get topics manager")
            } else {
                val shouldRecordObservation = false
                topicsRequestBuilder  = GetTopicsRequest.Builder()
                topicsRequestBuilder.setAdsSdkName(baseContext.packageName)
                topicsRequestBuilder.setShouldRecordObservation(shouldRecordObservation)
            }
        } else {
            topicsResultText = "Could not get topics manager"
            Log.e(TAG, "Privacy Sandbox Ads API not available.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTopics()
        enableEdgeToEdge()
        setContent {
            Privacy_ads_consumerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)){
                        Button(
                            onClick = {
                                lifecycle.coroutineScope.launch {
                                    try {
                                        val result = topicsManager?.getTopics(
                                            topicsRequestBuilder.build(),
                                        )
                                        if (result != null) {
                                            val topicsResult = result.topics
                                            if (topicsResult.isEmpty()) {
                                                topicsResultText = "Returned Empty"
                                                Log.i("Topic", "Returned Empty")
                                            } else {
                                                topicsResultText = ""
                                                for (i in topicsResult.indices) {
                                                    topicsResultText += topicsResult[i].topicId.toString()
                                                    Log.i(
                                                        "Topic",
                                                        topicsResult[i].topicId.toString()
                                                    )
                                                }
                                            }
                                        }
                                    } catch (ex: SecurityException) {
                                        topicsResultText = "$ex"
                                        Log.e(TAG, "Security Exception", ex)
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            Text("Get Topics")
                        }
                        Text(topicsResultText)
                    }
                }
            }
        }
    }
}
