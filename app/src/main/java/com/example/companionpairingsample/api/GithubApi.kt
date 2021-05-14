package com.example.companiondevicepairing.api

import retrofit2.Call
import retrofit2.http.GET

interface GithubApi {
    @GET("resources/dogs?number=1")
    fun listRepos(): Call<List<Any>>
}