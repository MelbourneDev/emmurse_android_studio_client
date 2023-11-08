package com.mattvu.emmurse.register.service

import com.mattvu.emmurse.register.models.PersonalUserData
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST


interface PersonalRegistrationAPI {
    @POST("personal")
    fun registerPersonal(@Body registrationRequest: PersonalUserData): Call<ResponseBody>

}
