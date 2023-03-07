package com.example.backendlike.repository

import com.example.backendlike.model.Like
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface LikeRepository :ReactiveMongoRepository<Like, String> {
    fun findLikeByUserID(userId: String): Flux<Like>
    fun deleteByUserIDAndProductId(userId: String, productId: String): Mono<Void>
    fun existsByUserIDAndProductId(userId: String, productId: String): Mono<Boolean>
}