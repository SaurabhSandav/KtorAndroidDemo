package com.redridgeapps.ktorandroiddemo.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Listing(
    @Json(name = "kind") val kind: String,
    @Json(name = "data") val data: Data
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "modhash") val modhash: String,
    @Json(name = "dist") val dist: Int,
    @Json(name = "children") val children: List<Child>,
    @Json(name = "after") val after: String
)

@JsonClass(generateAdapter = true)
data class Child(
    @Json(name = "kind") val kind: String,
    @Json(name = "data") val post: Post
)

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "title") val title: String
)