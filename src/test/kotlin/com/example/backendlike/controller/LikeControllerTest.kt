package com.example.backendlike.controller

import com.example.backendlike.fixture.Pageable
import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import com.example.backendlike.service.LikeService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.data.domain.PageRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

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
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like.userId, like.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = like.productId,
            userId = like.userId
        ))
            .expectNext(true)
            .verifyComplete()
    }
    @Test
    fun `should return false if like does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like.userId, like.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = like.productId,
            userId = like.userId
        ))
            .expectNext(false)
            .verifyComplete()
    }
    @Test
    fun `should return all likes by user if it exist in database`() {
        val size = 10L
        val index = 0L
        val expected = Pageable(
            nextPage = null,
            size = size,
            data = listOf(like)
        )
        doReturn(Flux.just(like))
            .`when`(likeRepository)
            .findLikeByUserId(like.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(1L))
            .`when`(likeRepository)
            .countLikeByUserId(like.userId)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like.userId,
            size = Optional.of(size.toInt()),
            page = Optional.of(index.toInt())
        ))
            .expectNext(expected)
            .verifyComplete()
    }
    @Test
    fun `should return all likes with next index by user if it exist in database`() {
        val size = 1L
        val index = 0L
        val expected = Pageable(
            nextPage = 1,
            size = size,
            data = listOf(like, like)
        )
        doReturn(Flux.just(like, like))
            .`when`(likeRepository)
            .findLikeByUserId(like.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(2L))
            .`when`(likeRepository)
            .countLikeByUserId(like.userId)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like.userId,
            size = Optional.of(size.toInt()),
            page = Optional.of(index.toInt())
        ))
            .expectNext(expected)
            .verifyComplete()
    }
    @Test
    fun `should return empty like list by user if it does not exist in database`() {
        val size = 10L
        val index = 0L
        val expected = Pageable(
            nextPage = null,
            size = size,
            data = emptyList<Like>()
        )
        doReturn(Flux.empty<Like>())
            .`when`(likeRepository)
            .findLikeByUserId(like.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(1L))
            .`when`(likeRepository)
            .countLikeByUserId(like.userId)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like.userId,
            size = Optional.of(size.toInt()),
            page = Optional.of(index.toInt())
        ))
            .expectNext(expected)
            .verifyComplete()
    }
}