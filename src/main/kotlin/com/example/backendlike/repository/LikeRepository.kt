package com.example.backendlike.repository

import com.example.backendlike.model.Like
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface LikeRepository :ReactiveMongoRepository<Like, String> {
    fun findLikeByUserId(userId: String, pageRequest: PageRequest): Flux<Like>
    fun countLikeByUserId(userId: String): Mono<Long>
    fun deleteByUserIdAndProductId(userId: String, productId: String): Mono<Void>
    fun existsByUserIdAndProductId(userId: String, productId: String): Mono<Boolean>
}