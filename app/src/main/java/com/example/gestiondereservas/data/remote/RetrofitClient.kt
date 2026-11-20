package com.example.gestiondereservas.data.remote

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter, value: LocalDate?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): LocalDate? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            LocalDate.parse(reader.nextString())
        }
    }
}

class LocalTimeAdapter : TypeAdapter<LocalTime>() {
    override fun write(out: JsonWriter, value: LocalTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): LocalTime? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            LocalTime.parse(reader.nextString())
        }
    }
}
object RetrofitClient {

    private const val BASE_URL_RESERVA = "http://98.95.72.24:8082"
    private const val BASE_URL_AUDITORIA = "http://98.95.72.24:8082/"
    private const val BASE_URL_LOGIN = "http://98.95.72.24:8085/"
    private const val BASE_URL_SALAS = "http://98.95.72.24:8084/"
    private const val BASE_URL_HORARIOS = "http://98.95.72.24:8083/"


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(com.example.gestiondereservas.data.remote.RetrofitClient.logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    val api: ReservaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_RESERVA)
            .client(okHttp)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ReservaApiService::class.java)
    }

    val apiAuditoria: AuditoriaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AUDITORIA)
            .client(okHttp)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuditoriaApiService::class.java)
    }
}