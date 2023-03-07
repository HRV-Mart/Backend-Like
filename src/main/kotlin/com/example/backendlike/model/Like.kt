package com.example.backendlike.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document("Like")
@CompoundIndex(name = "like_idx", def = "{'userId': 1, 'productId': 1}",
    unique = true)
data class Like (
    val userID: String,
    val productId: String
)