package com.example.privacy_ads_consumer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.coroutineScope
import androidx.privacysandbox.sdkruntime.client.SdkSandboxManagerCompat
import com.example.privacy_ads_consumer.ui.theme.Privacy_ads_consumerTheme
import kotlinx.coroutines.launch

private const val TAG = "privacy_ads_consumer"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Privacy_ads_consumerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = {
                            lifecycle.coroutineScope.launch {
                                Log.e(TAG, "onCreate: Calling SDK")
                                val remoteSdk = SdkSandboxManagerCompat.from(this@MainActivity).loadSdk(
                                    sdkName = "androidx.privacysandbox.ads.adservices",
                                    params = Bundle.EMPTY
                                )
                                Log.e(TAG, "onCreate: SDK Loaded: $remoteSdk")
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        Text("Load SDK")
                    }
                }
            }
        }
    }
}
