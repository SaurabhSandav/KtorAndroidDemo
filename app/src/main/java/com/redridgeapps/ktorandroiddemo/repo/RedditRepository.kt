package com.redridgeapps.ktorandroiddemo.repo

import com.readystatesoftware.chuck.ChuckInterceptor
import com.redridgeapps.ktorandroiddemo.BuildConfig
import com.redridgeapps.ktorandroiddemo.model.AuthResult
import com.redridgeapps.ktorandroiddemo.model.Listing
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.HttpPlainText
import io.ktor.client.features.auth.basic.BasicAuth
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import java.util.*

class RedditRepository(
    private val jsonSerializer: JsonSerializer,
    private val prefRepo: PreferenceRepository,
    chuckInterceptor: ChuckInterceptor
) {

    private lateinit var authResult: AuthResult

    private val client: HttpClient = HttpClient(OkHttp) {
        engine { addInterceptor(chuckInterceptor) }

        install(HttpPlainText) { defaultCharset = Charsets.UTF_8 }
        install(JsonFeature) { serializer = jsonSerializer }

        install(BasicAuth) {
            username = BuildConfig.CLIENT_ID
            password = ""
        }

        defaultRequest {
            if (::authResult.isInitialized && authResult.isValid) {
                header(LABEL_HEADER_AUTHORIZATION, "${authResult.tokenType} ${authResult.accessToken}")
            }
        }
    }

    suspend fun getAndroidDevHot(): Listing {
        refreshAccessToken()

        return client.get(REDDIT_OAUTH_BASE_URL + ENDPOINT_ANDROIDDEV_HOT) {
            parameter(LABEL_PARAMETER_LIMIT, 100)
        }
    }

    private suspend fun refreshAccessToken() {

        prefRepo.authResult?.let { authResult = it }

        if (::authResult.isInitialized && authResult.isValid) return

        authResult = client.submitForm {
            url(REDDIT_ACCESS_TOKEN_REQUEST_URL)
            parameter(LABEL_PARAMETER_GRANT_TYPE, APP_GRANT)
            parameter(LABEL_PARAMETER_DEVICE_ID, getDeviceId())
        }

        prefRepo.authResult = authResult
    }

    private fun getDeviceId() = prefRepo.deviceId ?: run {
        UUID.randomUUID().toString().apply { prefRepo.deviceId = this }
    }
}

private const val REDDIT_ACCESS_TOKEN_REQUEST_URL = "https://www.reddit.com/api/v1/access_token"
private const val REDDIT_OAUTH_BASE_URL = "https://oauth.reddit.com"

private const val ENDPOINT_ANDROIDDEV_HOT = "/r/androiddev/hot"

private const val LABEL_HEADER_AUTHORIZATION = "Authorization"

private const val LABEL_PARAMETER_GRANT_TYPE = "grant_type"
private const val LABEL_PARAMETER_DEVICE_ID = "device_id"
private const val LABEL_PARAMETER_LIMIT = "limit"

private const val APP_GRANT = "https://oauth.reddit.com/grants/installed_client"