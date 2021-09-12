package de.tierwohlteam.android.plantraindoc_v1.repositories.remote

import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests.AccountRequest
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests.GoalRequest
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.requests.IDRequest
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.responses.SimpleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PTDapi {

    @POST("/register")
    suspend fun register(
        @Body registerRequest: AccountRequest) : Response<SimpleResponse>

    @POST("/login")
    suspend fun login(
        @Body loginRequest: AccountRequest) : Response<SimpleResponse>

    @POST("/loginWeb")
    suspend fun getUserID(
        @Body idRequest: IDRequest): Response<User>

    @GET("/goals/app")
    suspend fun goals(
        @Query("date") date: String): List<Goal>

    @POST("/goals/app")
    suspend fun insertGoal(
        @Body goals: List<Goal>): Response<SimpleResponse>

    @GET("/plans/sync")
    suspend fun plans(
        @Query("date") date: String): List<PlanWithRelations>

    @POST("/plans/sync")
    suspend fun insertPlans(
        @Body plans: List<PlanWithRelations>): Response<SimpleResponse>

    @POST("sessions/sync")
    suspend fun insertSessions(
        @Body newSessions: List<SessionWithRelations>): Response<SimpleResponse>

}