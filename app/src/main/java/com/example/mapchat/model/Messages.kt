package com.example.mapchat.model

data class Messages(
    val fromUuid: String?,
    val toUuid: String?,
    val text: String?,
    val date: String?,
    val friendImage: String?
) {
    constructor() : this("", "", "", "", "")
}