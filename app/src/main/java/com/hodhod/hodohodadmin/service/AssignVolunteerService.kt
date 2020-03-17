package com.hodhod.hodohodadmin.service

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface AssignVolunteerService {
    @POST("predict")
    fun assignVolunteer(@Body body: AssignVolunteerBody): Observable<AssignVolunteerResponse>
}

data class AssignVolunteerBody(val features: IntArray)
data class AssignVolunteerResponse(val volunteer: String)
