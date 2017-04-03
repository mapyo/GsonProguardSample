package com.mapyo.gsonproguardsample

import com.google.gson.annotations.SerializedName


data class Repository(
        val id: Int,
        val name: String,

        @SerializedName("html_url")
        val htmlUrl: String,

        val description: String?
)
