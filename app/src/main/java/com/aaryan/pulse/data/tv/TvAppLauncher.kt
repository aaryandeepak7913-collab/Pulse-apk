package com.aaryan.pulse.data.tv

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.CompletableDeferred
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.concurrent.TimeUnit

/** Local-network app launching for Google/Android TV (DIAL) and LG webOS (SSAP). */
object TvAppLauncher {
    enum class Platform { GOOGLE_TV, LG_WEBOS }
    enum class App(val label: String, val dialIds: List<String>, val lgId: String) {
        NETFLIX("Netflix", listOf("Netflix"), "netflix"),
        YOUTUBE("YouTube", listOf("YouTube"), "youtube.leanback.v4"),
        JIOHOTSTAR("JioHotstar", listOf("JioHotstar", "Hotstar", "JioCinema"), "com.hotstar.app"),
        PRIME_VIDEO("Prime Video", listOf("AmazonInstantVideo", "PrimeVideo"), "amazon")
    }

    fun savedAddress(context: Context): String = prefs(context).getString("tv_ip", "") ?: ""
    fun savedPlatform(context: Context): Platform = runCatching {
        Platform.valueOf(prefs(context).getString("tv_platform", Platform.GOOGLE_TV.name)!!)
    }.getOrDefault(Platform.GOOGLE_TV)
    fun saveTarget(context: Context, platform: Platform, address: String) {
        prefs(context).edit().putString("tv_platform", platform.name).putString("tv_ip", address.trim()).apply()
    }

    suspend fun launch(context: Context, platform: Platform, address: String, app: App): String = withContext(Dispatchers.IO) {
        require(address.isNotBlank()) { "Enter your TV's IP address." }
        when (platform) {
            Platform.GOOGLE_TV -> launchDial(address.trim(), app)
            Platform.LG_WEBOS -> launchWebOs(context, address.trim(), app)
        }
    }

    private fun launchDial(host: String, app: App): String {
        var lastCode = -1
        for (dialId in app.dialIds) {
            val connection = (URL("http://$host:8008/apps/$dialId").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 4500
                readTimeout = 4500
                doOutput = true
                setRequestProperty("Content-Length", "0")
            }
            try {
                val code = connection.responseCode
                lastCode = code
                if (code == 200 || code == 201 || code == 202) return "Launch request sent to ${app.label}."
                if (code != 404) return "TV replied with HTTP $code while launching ${app.label}. Check the TV IP and local network."
            } finally { connection.disconnect() }
        }
        return if (lastCode == 404) "${app.label} is not exposed through DIAL on this TV. The TV may support it through Android TV Remote pairing, which is not part of this app-shortcuts build."
        else "Couldn't reach the TV's DIAL service on port 8008. Check the IP and make sure both devices are on the same Wi-Fi."
    }

    private suspend fun launchWebOs(context: Context, host: String, app: App): String = withTimeout(18000) {
        val savedKey = prefs(context).getString("lg_client_key", null)
        val result = CompletableDeferred<String>()
        val client = object : WebSocketClient(URI("ws://$host:3000")) {
            private var launchSent = false
            override fun onOpen(handshake: ServerHandshake?) {
                val payload = JSONObject().put("forcePairing", false).put("pairingType", "PROMPT")
                    .put("manifest", JSONObject().put("manifestVersion", 1).put("appVersion", "1.0")
                        .put("signed", JSONObject().put("created", "20260101").put("appId", "com.aaryan.pulse")
                            .put("vendorId", "com.aaryan").put("localizedAppNames", JSONObject().put("", "Pulse"))
                            .put("localizedVendorNames", JSONObject().put("", "Aaryan"))
                            .put("permissions", org.json.JSONArray(listOf("LAUNCH", "READ_INSTALLED_APPS")))))
                if (savedKey != null) payload.put("client-key", savedKey)
                send(JSONObject().put("type", "register").put("id", "pulse-register").put("payload", payload).toString())
            }
            override fun onMessage(message: String) {
                runCatching {
                    val json = JSONObject(message)
                    when {
                        json.optString("type") == "registered" -> {
                            val key = json.optJSONObject("payload")?.optString("client-key")
                            if (!key.isNullOrBlank()) prefs(context).edit().putString("lg_client_key", key).apply()
                            if (!launchSent) {
                                launchSent = true
                                send(JSONObject().put("type", "request").put("id", "pulse-list-apps")
                                    .put("uri", "ssap://com.webos.applicationManager/listLaunchPoints")
                                    .put("payload", JSONObject()).toString())
                            }
                        }
                        json.optString("id") == "pulse-list-apps" -> {
                            val points = json.optJSONObject("payload")?.optJSONArray("launchPoints")
                            val wanted = when (app) {
                                App.NETFLIX -> listOf("netflix")
                                App.YOUTUBE -> listOf("youtube")
                                App.JIOHOTSTAR -> listOf("hotstar", "jio")
                                App.PRIME_VIDEO -> listOf("prime", "amazon")
                            }
                            var selectedId: String? = null
                            if (points != null) {
                                for (index in 0 until points.length()) {
                                    val point = points.optJSONObject(index) ?: continue
                                    val searchable = (point.optString("title") + " " + point.optString("id")).lowercase()
                                    if (wanted.any { searchable.contains(it) }) {
                                        selectedId = point.optString("id").takeIf { it.isNotBlank() }
                                        break
                                    }
                                }
                            }
                            if (points != null && selectedId == null) {
                                result.complete("${app.label} wasn't found in the apps installed on this LG TV.")
                                close()
                            } else {
                                val appId = selectedId ?: app.lgId
                                send(JSONObject().put("type", "request").put("id", "pulse-launch")
                                    .put("uri", "ssap://com.webos.applicationManager/launch")
                                    .put("payload", JSONObject().put("id", appId)).toString())
                            }
                        }
                        json.optString("id") == "pulse-launch" -> {
                            val error = json.optString("error", "")
                            if (error.isNotBlank()) result.complete("TV rejected ${app.label}: $error")
                            else result.complete("Launch request sent to ${app.label}.")
                            close()
                        }
                        json.optString("type") == "error" -> result.complete("LG TV pairing/launch error: ${json.optString("error", "unknown error")}")
                    }
                }
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                if (!result.isCompleted) result.completeExceptionally(IllegalStateException(reason ?: "TV closed the connection"))
            }
            override fun onError(ex: Exception?) {
                if (!result.isCompleted) result.completeExceptionally(ex ?: IllegalStateException("WebSocket connection failed"))
            }
        }
        client.connectBlocking(7, TimeUnit.SECONDS)
        if (!client.isOpen) throw IllegalStateException("Couldn't connect to the LG TV. Check the IP and make sure the phone and TV are on the same Wi-Fi.")
        try { result.await() } finally { if (client.isOpen) client.close() }
    }

    private fun prefs(context: Context) = context.getSharedPreferences("pulse_tv_apps", Context.MODE_PRIVATE)
}
