package ua.polytech.testingtask.api.interceptors

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import ua.polytech.testingtask.R

class BearerInterceptor(private val context: Context) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get the original URL and create a new URL with the added query parameter
        val originalHttpUrl = originalRequest.url
        val newUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("api-key", context.getString(R.string.api_key))
            .build()

        // Create a new request with the modified URL
        val authorisedRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(authorisedRequest)
    }
}