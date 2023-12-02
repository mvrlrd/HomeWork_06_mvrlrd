package otus.homework.reactivecats

import android.content.Context
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DiContainer {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://meowfacts.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava3CallAdapterFactory.create())
            .build()
    }

    val service by lazy { retrofit.create(CatsService::class.java) }

    fun localCatFactsGenerator(context: Context) = LocalCatFactsGenerator(context)
}