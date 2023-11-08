package com.mattvu.emmurse.user_settings.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object UserSettingsApiClient {
    private val gson = GsonBuilder().setLenient().create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // Update the base URL to match your server
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val service: UserSettingsAPI = retrofit.create(UserSettingsAPI::class.java)
}
