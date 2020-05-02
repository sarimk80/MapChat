package com.example.mapchat.model


data class Messages(
    val fromUuid: String?,
    val toUuid: String?,
    val text: String?,
    val date: String?,
    val friendImage: String?,
    val gifUrl: String?,
    val imageUrl: String?,
    val type: String?
) {
    constructor() : this("", "", "", "", "", "", "", "")
}