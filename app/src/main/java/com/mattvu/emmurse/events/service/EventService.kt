package com.mattvu.emmurse.events.service
import com.mattvu.emmurse.events.models.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventAPI {

    @POST("book")
    suspend fun bookEvent(@Body event: Event): Response<Event>

    @GET("userEvents")
    suspend fun getUserEvents(@Query("userName") userName: String): Response<List<Event>>

    @DELETE("cancel/{eventId}")
    suspend fun cancelEvent(@Path("eventId") eventId: Long): Response<Void>

}


