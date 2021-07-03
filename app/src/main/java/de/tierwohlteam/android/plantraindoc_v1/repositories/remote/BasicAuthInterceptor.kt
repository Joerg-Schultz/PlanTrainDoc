package de.tierwohlteam.android.plantraindoc_v1.repositories.remote

import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.IGNORE_AUTH_URLS
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    val id: Uuid? = null
    val password: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if(request.url.encodedPath in IGNORE_AUTH_URLS){
            return chain.proceed(request)
        }
        val authenticatedRequest = request.newBuilder()
            .header("Authorization",
                Credentials.basic(id?.toString() ?: "",password ?: ""))
            .build()
        return chain.proceed(authenticatedRequest)
    }
}