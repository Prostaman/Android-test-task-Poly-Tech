package ua.polytech.testingtask.api.di

import android.content.Context
import ua.polytech.testingtask.api.client.CatalogClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.polytech.testingtask.R
import ua.polytech.testingtask.api.client.ListOfBooksClient
import ua.polytech.testingtask.api.interceptors.BearerInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object BackendModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .addNetworkInterceptor(BearerInterceptor(context))
            .build()


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient,@ApplicationContext context: Context): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.domain))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideCatalogClient(retrofit: Retrofit): CatalogClient = retrofit.create(CatalogClient::class.java)

    @Singleton
    @Provides
    fun provideListOfBooksClient(retrofit: Retrofit): ListOfBooksClient = retrofit.create(ListOfBooksClient::class.java)

}