package com.redridgeapps.ktorandroiddemo.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResult(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "expires_in") val expiresIn: Long,
    @Json(name = "scope") val scope: String,
    val generatedAt: Long = (System.currentTimeMillis() / 1000)
) {
    val isValid: Boolean
        get() = ((System.currentTimeMillis() / 1000) - generatedAt) < expiresIn

    // To access generated extension -> `AuthResult.jsonAdapter(moshi)`
    companion object
}
