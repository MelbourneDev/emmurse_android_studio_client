package com.mattvu.emmurse.register.service

import com.mattvu.emmurse.register.models.BusinessUserData
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface BusinessRegistrationAPI {
    @POST("business")
    fun registerBusiness(@Body registrationRequest: BusinessUserData): Call<ResponseBody>
}
