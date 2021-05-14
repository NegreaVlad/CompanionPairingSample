package com.example.companiondevicepairing.helpers

import com.example.companiondevicepairing.api.GithubApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object GithubApiProvider {
    fun getApi(): GithubApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog-facts-api.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GithubApi::class.java)
    }
}