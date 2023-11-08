package com.mattvu.emmurse.user_settings.service

import com.mattvu.emmurse.user_settings.models.UserSettingsData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserSettingsAPI {

    @GET("/users/{username}")
    fun getUserData(@Path("username") username: String): Call<UserSettingsData>

    @POST("/users/update/{username}")
    fun updateUserData(@Path("username") username: String, @Body userData: UserSettingsData): Call<ResponseBody>

}