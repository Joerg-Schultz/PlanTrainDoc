package de.tierwohlteam.android.plantraindoc_v1.repositories.remote

import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests.AccountRequest
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.responses.SimpleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PTDapi {

    @POST("/register")
    suspend fun register(
        @Body registerRequest: AccountRequest) : Response<SimpleResponse>
}