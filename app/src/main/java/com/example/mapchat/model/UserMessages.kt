package com.example.mapchat.model

data class UserMessages(
    val friendUser: Users?,
    val date: String?,
    val userId: String?
) {
    constructor() : this(null, "", "")
}