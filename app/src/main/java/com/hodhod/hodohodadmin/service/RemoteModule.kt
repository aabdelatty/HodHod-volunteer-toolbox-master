package com.hodhod.hodohodadmin.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {
    single {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(CloudConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CloudConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CloudConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        builder.build()
    }
    single { GsonBuilder().create() }
    single {
        Retrofit.Builder()
            .baseUrl(CloudConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .build()
    }

    factory { get<Retrofit>().create(AssignVolunteerService::class.java) }


}