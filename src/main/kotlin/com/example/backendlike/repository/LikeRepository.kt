package com.example.backendlike.repository

import com.example.backendlike.model.Like
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository :ReactiveMongoRepository<Like, String>