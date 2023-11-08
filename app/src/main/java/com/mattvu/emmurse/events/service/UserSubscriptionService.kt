package com.mattvu.emmurse.events.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserSubscriptionService {
    @GET("/api/users/subscription/{username}")
    fun getUserSubscription(@Path("username") username: String): Call<String>
}
