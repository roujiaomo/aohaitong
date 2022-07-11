package com.aohaitong.http

import com.google.gson.annotations.SerializedName

data class ApiException(
    @field:SerializedName("message") val messageError: String = "",
    @field:SerializedName("errors") val errors: List<String> = emptyList(),
    @field:SerializedName("status") val status: String = "",
    @field:SerializedName("latest_version") val latestVersion: String = "",
    @field:SerializedName("update_url") val updateUrl: String = ""
) : Throwable(messageError) {
    var statusCode: Int? = null
}
