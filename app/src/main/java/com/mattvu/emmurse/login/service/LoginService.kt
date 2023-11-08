package com.mattvu.emmurse.login.service

import com.mattvu.emmurse.login.models.LoginData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST



interface LoginUserAPI {
@POST("user")
fun LoginUser(@Body registrationRequest: LoginData): Call<ResponseBody>

}