package com.example.backendlike.controller

import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import com.example.backendlike.service.LikeService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class LikeControllerTest {
    private val likeRepository = mock(LikeRepository::class.java)
    private val response = mock(ServerHttpResponse::class.java)
    private val likeService = LikeService(likeRepository)
    private val likeController = LikeController(likeService)
    private val like = Like(
        userId = "userID",
        productId = "productID"
    )
    @Test
    fun `should insert like in database when not exist in database`() {
        doReturn(Mono.just(like)).`when`(likeRepository).insert(like)
        StepVerifier.create(likeController.addProductToLike(like, response))
            .expectNext("Like added successfully")
            .verifyComplete()
    }
    @Test
    fun `should not insert like in database when it exist in database`() {
        doReturn(Mono.error<Exception>(Exception("Like already exist")))
            .`when`(likeRepository)
            .insert(like)
        StepVerifier.create(likeController.addProductToLike(like, response))
            .expectNext("Like already exist")
            .verifyComplete()
    }
    @Test
    fun `should remove like from database when it does not exist in database`() {
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like.userId, like.productId)
        doReturn(Mono.empty<Void>())
            .`when`(likeRepository)
            .deleteByUserIdAndProductId(like.userId, like.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = like.userId,
            productId = like.productId,
            response = response
        ))
            .expectNext("Like removed successfully")
            .verifyComplete()
    }
    @Test
    fun `should not remove like from database if it does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like.userId, like.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = like.userId,
            productId = like.productId,
            response = response
        ))
            .expectNext("Like not found")
            .verifyComplete()
    }
    @Test
    fun `should return true if like exist in database`() {

    }
    @Test
    fun `should return false if like does not exist in database`() {

    }
    @Test
    fun `should return all likes by user if it exist in database`() {

    }
    @Test
    fun `should return empty like list by user if it does not exist in database`() {

    }
}