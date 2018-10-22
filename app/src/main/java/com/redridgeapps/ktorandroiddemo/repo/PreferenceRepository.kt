package com.redridgeapps.ktorandroiddemo.repo

import android.content.SharedPreferences
import androidx.core.content.edit
import com.redridgeapps.ktorandroiddemo.model.AuthResult
import com.redridgeapps.ktorandroiddemo.model.jsonAdapter
import com.squareup.moshi.Moshi

class PreferenceRepository(
    private val moshi: Moshi,
    private val prefs: SharedPreferences
) {

    var deviceId: String?
        get() = prefs.getString(KEY_DEVICE_ID, null)
        set(value) = prefs.edit { putString(KEY_DEVICE_ID, value) }

    var authResult: AuthResult?
        get() = prefs.getString(KEY_AUTH_RESULT, null)?.let {
            AuthResult.jsonAdapter(moshi).fromJson(it)
        }
        set(value) = prefs.edit { putString(KEY_AUTH_RESULT, AuthResult.jsonAdapter(moshi).toJson(value)) }
}

private const val KEY_DEVICE_ID = "device_id"
private const val KEY_AUTH_RESULT = "auth_result"