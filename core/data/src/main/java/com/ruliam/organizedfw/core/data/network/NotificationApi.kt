package com.ruliam.organizedfw.core.data.network

import com.ruliam.organizedfw.core.data.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    companion object{
        const val CONTENT_TYPE = "application/json"
        //TODO REMOVER A API KEY DAQ
        const val API_KEY = "AAAAZKCLIO8:APA91bFQEgqtDPQE61E3fJ8QkhNkn8pze-04LIiqj_wdrlTuQbfMc_rfdNKtU5aC1ADUgBUt2gVVUNRve4N-zVcmIYJHlGSYvRE0e4q5YijYNC8B6lzbSw3WhOAGLqgSn7MDF6KaJ83X"
    }

    @Headers("Authorization: key=${API_KEY}", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}
