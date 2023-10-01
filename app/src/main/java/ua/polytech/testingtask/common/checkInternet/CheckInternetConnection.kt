package ua.polytech.testingtask.common.checkInternet

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
//CheckInternet
@SuppressLint("MissingPermission")
fun isConnectedToInternet(context: Context): Boolean {
    var result = false// Returns connection type. 0: none; 1: mobile data; 2: wifi
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    result = true
                }
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    result = true
                }
                hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                    result = true
                }
            }
        }
    }
    return result
}