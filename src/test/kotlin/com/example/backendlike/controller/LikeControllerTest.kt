package com.example.backendlike.controller

import com.hrv.mart.custompageable.Pageable
import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import com.example.backendlike.service.LikeService
import com.hrv.mart.product.Product
import com.hrv.mart.product.repository.ProductRepository
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
    private val productRepository = mock(ProductRepository::class.java)
    private val likeService = LikeService(likeRepository, productRepository)
    private val likeController = LikeController(likeService)
    private val like1 = Like(
        userId = "userID",
        productId = "test_image_1"
    )
    private val like2 = Like(
        userId = "userID",
        productId = "test_image_2"
    )
    private val product1 = Product(
        name = "Test Product",
        description = "Only for testing",
        images = listOf("https://image.hrv-mart.com/test_image_1"),
        price = 100,
        id = "test_image_1"
    )
    private val product2 = Product(
        name = "Test Product 2",
        description = "Only for testing",
        images = listOf("https://image.hrv-mart.com/test_image_2"),
        price = 100,
        id = "test_image_2"
    )
    @Test
    fun `should insert like in database when not exist in database`() {
        doReturn(Mono.just(like1)).`when`(likeRepository).insert(like1)
        StepVerifier.create(likeController.addProductToLike(like1, response))
            .expectNext("Like added successfully")
            .verifyComplete()
    }
    @Test
    fun `should not insert like in database when it exist in database`() {
        doReturn(Mono.error<Exception>(Exception("Like already exist")))
            .`when`(likeRepository)
            .insert(like1)
        StepVerifier.create(likeController.addProductToLike(like1, response))
            .expectNext("Like already exist")
            .verifyComplete()
    }
    @Test
    fun `should remove like from database when it does not exist in database`() {
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like1.userId, like1.productId)
        doReturn(Mono.empty<Void>())
            .`when`(likeRepository)
            .deleteByUserIdAndProductId(like1.userId, like1.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = like1.userId,
            productId = like1.productId,
            response = response
        ))
            .expectNext("Like removed successfully")
            .verifyComplete()
    }
    @Test
    fun `should not remove like from database if it does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like1.userId, like1.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = like1.userId,
            productId = like1.productId,
            response = response
        ))
            .expectNext("Like not found")
            .verifyComplete()
    }
    @Test
    fun `should return true if like exist in database`() {
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like1.userId, like1.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = like1.productId,
            userId = like1.userId
        ))
            .expectNext(true)
            .verifyComplete()
    }
    @Test
    fun `should return false if like does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(like1.userId, like1.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = like1.productId,
            userId = like1.userId
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
            data = listOf(product1, product2)
        )
        doReturn(Flux.just(like1, like2))
            .`when`(likeRepository)
            .findLikeByUserId(like1.userId, PageRequest.of(index.toInt(), size.toInt()))

        doReturn(Mono.just(2L))
            .`when`(likeRepository)
            .countLikeByUserId(like1.userId)

        doReturn(Mono.just(product1))
            .`when`(productRepository)
            .getProductByProductId(product1.id)
        doReturn(Mono.just(product2))
            .`when`(productRepository)
            .getProductByProductId(product2.id)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like1.userId,
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
            data = listOf(product1)
        )
        doReturn(Flux.just(like1))
            .`when`(likeRepository)
            .findLikeByUserId(like1.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(2L))
            .`when`(likeRepository)
            .countLikeByUserId(like1.userId)
        doReturn(Mono.just(product1))
            .`when`(productRepository)
            .getProductByProductId(product1.id)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like1.userId,
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
            data = emptyList<Product>()
        )
        doReturn(Flux.empty<String>())
            .`when`(likeRepository)
            .findLikeByUserId(like1.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(1L))
            .`when`(likeRepository)
            .countLikeByUserId(like1.userId)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = like1.userId,
            size = Optional.of(size.toInt()),
            page = Optional.of(index.toInt())
        ))
            .expectNext(expected)
            .verifyComplete()
    }
}