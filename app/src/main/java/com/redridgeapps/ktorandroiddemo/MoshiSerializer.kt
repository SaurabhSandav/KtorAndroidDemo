package com.redridgeapps.ktorandroiddemo

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.ktor.client.call.TypeInfo
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent

class MoshiSerializer(private val moshi: Moshi) : JsonSerializer {

    override fun write(data: Any): OutgoingContent = TextContent(
        moshi.adapter<Any>(Types.getRawType(data.javaClass)).toJson(data),
        ContentType.Application.Json
    )

    override suspend fun read(type: TypeInfo, response: HttpResponse): Any {
        return moshi.adapter(Types.getRawType(type.reifiedType)).fromJson(response.readText())!!
    }
}