package com.example

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AdminRewardConfig {
    private const val PROJECT_ID = "wise-digit-pwx5p"
    private const val DATABASE_ID = "ai-studio-remixinstabooste-83e7a84b-5c64-4d67-8c29-b0fcbe88a9c4"
    private const val API_KEY = "AIzaSyDb24vjKPqeRjPZ4dnpWkrFrWSNvlvQyfM"

    @Volatile
    var coinsPerRewardAd: Int = 0
        private set

    fun initialize() {
        Thread {
            try {
                val url = URL(
                    "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/$DATABASE_ID/documents/config/global?key=$API_KEY"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val json = JSONObject(
                        connection.inputStream.bufferedReader().use { it.readText() }
                    )

                    val ads = json.getJSONObject("fields")
                        .getJSONObject("ads")
                        .getJSONObject("mapValue")
                        .getJSONObject("fields")

                    coinsPerRewardAd = ads
                        .getJSONObject("coinsPerRewardAd")
                        .getString("integerValue")
                        .toInt()
                        .coerceAtLeast(0)
                }

                connection.disconnect()
            } catch (_: Exception) {
                coinsPerRewardAd = 0
            }
        }.start()
    }
}
