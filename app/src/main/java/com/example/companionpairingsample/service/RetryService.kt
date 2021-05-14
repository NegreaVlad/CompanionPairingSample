package com.example.companiondevicepairing.service

import android.app.Service
import android.companion.CompanionDeviceManager
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.companiondevicepairing.api.GithubApi
import com.example.companiondevicepairing.helpers.GithubApiProvider
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "RetryService"

class RetryService: Service() {

    private val githubApi: GithubApi by lazy {
        GithubApiProvider.getApi()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Service starting")

        startProcessing()

        return START_STICKY
    }

    private fun startProcessing() {
        GlobalScope.launch {
            var counter = 0
            while (isActive) {
                if (counter > 50)
                    stopSelf()
//                Log.d(TAG, "Service counter $counter")
                counter++
                launch(Dispatchers.IO) {
                    githubApi.listRepos().enqueue(object: Callback<List<Any>> {
                        override fun onResponse(
                            call: Call<List<Any>>,
                            response: Response<List<Any>>
                        ) {
                            Log.d(TAG, "Api response $response")
                        }

                        override fun onFailure(call: Call<List<Any>>, t: Throwable) {
                            Log.d(TAG, "Api call failure $t")
                        }

                    })
                }
                delay(2000)
            }
        }
    }

    override fun onDestroy() {
        Toast.makeText(this, "service stopping", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Service stopping")
    }
    override fun onBind(intent: Intent?): IBinder? = null
}