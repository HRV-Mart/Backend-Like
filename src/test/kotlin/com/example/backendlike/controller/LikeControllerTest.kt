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

    }
    @Test
    fun `should remove like from database when it does not exist in database`() {

    }
    @Test
    fun `should not remove like from database if it does not exist in database`() {

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
    fun `should return empoty like list by user if it does notx exist in database`() {

    }
}