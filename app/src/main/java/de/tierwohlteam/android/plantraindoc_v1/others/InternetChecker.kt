package de.tierwohlteam.android.plantraindoc_v1.others

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun checkForInternetConnection(context: Context): Boolean{
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasCapability(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasCapability(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasCapability(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasCapability(NetworkCapabilities.TRANSPORT_VPN) -> true
            else -> false
        }
    }
    return connectivityManager.activeNetworkInfo?.isAvailable ?: false
}