package com.example.backendlike

import com.example.backendlike.controller.LikeController
import com.example.backendlike.fixture.LikeFixture
import com.hrv.mart.custompageable.model.Pageable
import com.example.backendlike.repository.LikeRepository
import com.example.backendlike.service.LikeService
import com.hrv.mart.product.model.Product
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

    @Test
    fun `should insert like in database when not exist in database`() {
        doReturn(Mono.just(LikeFixture.like1)).`when`(likeRepository).insert(LikeFixture.like1)
        StepVerifier.create(likeController.addProductToLike(LikeFixture.like1, response))
            .expectNext("Like added successfully")
            .verifyComplete()
    }
    @Test
    fun `should not insert like in database when it exist in database`() {
        doReturn(Mono.error<Exception>(Exception("Like already exist")))
            .`when`(likeRepository)
            .insert(LikeFixture.like1)
        StepVerifier.create(likeController.addProductToLike(LikeFixture.like1, response))
            .expectNext("Like already exist")
            .verifyComplete()
    }
    @Test
    fun `should remove like from database when it does not exist in database`() {
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(LikeFixture.like1.userId, LikeFixture.like1.productId)
        doReturn(Mono.empty<Void>())
            .`when`(likeRepository)
            .deleteByUserIdAndProductId(LikeFixture.like1.userId, LikeFixture.like1.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = LikeFixture.like1.userId,
            productId = LikeFixture.like1.productId,
            response = response
        ))
            .expectNext("Like removed successfully")
            .verifyComplete()
    }
    @Test
    fun `should not remove like from database if it does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(LikeFixture.like1.userId, LikeFixture.like1.productId)
        StepVerifier.create(likeController.removeProductFromLike(
            userId = LikeFixture.like1.userId,
            productId = LikeFixture.like1.productId,
            response = response
        ))
            .expectNext("Like not found")
            .verifyComplete()
    }
    @Test
    fun `should return true if like exist in database`() {
        doReturn(Mono.just(true))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(LikeFixture.like1.userId, LikeFixture.like1.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = LikeFixture.like1.productId,
            userId = LikeFixture.like1.userId
        ))
            .expectNext(true)
            .verifyComplete()
    }
    @Test
    fun `should return false if like does not exist in database`() {
        doReturn(Mono.just(false))
            .`when`(likeRepository)
            .existsByUserIdAndProductId(LikeFixture.like1.userId, LikeFixture.like1.productId)
        StepVerifier.create(likeController.isProductLikedByUser(
            productId = LikeFixture.like1.productId,
            userId = LikeFixture.like1.userId
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
            data = listOf(LikeFixture.product1, LikeFixture.product2)
        )
        doReturn(Flux.just(LikeFixture.like1, LikeFixture.like2))
            .`when`(likeRepository)
            .findLikeByUserId(LikeFixture.like1.userId, PageRequest.of(index.toInt(), size.toInt()))

        doReturn(Mono.just(2L))
            .`when`(likeRepository)
            .countLikeByUserId(LikeFixture.like1.userId)

        doReturn(Mono.just(LikeFixture.product1))
            .`when`(productRepository)
            .getProductByProductId(LikeFixture.product1.id, null)
        doReturn(Mono.just(LikeFixture.product2))
            .`when`(productRepository)
            .getProductByProductId(LikeFixture.product2.id, null)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = LikeFixture.like1.userId,
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
            data = listOf(LikeFixture.product1)
        )
        doReturn(Flux.just(LikeFixture.like1))
            .`when`(likeRepository)
            .findLikeByUserId(LikeFixture.like1.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(2L))
            .`when`(likeRepository)
            .countLikeByUserId(LikeFixture.like1.userId)
        doReturn(Mono.just(LikeFixture.product1))
            .`when`(productRepository)
            .getProductByProductId(LikeFixture.product1.id, null)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = LikeFixture.like1.userId,
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
            .findLikeByUserId(LikeFixture.like1.userId, PageRequest.of(index.toInt(), size.toInt()))
        doReturn(Mono.just(1L))
            .`when`(likeRepository)
            .countLikeByUserId(LikeFixture.like1.userId)
        StepVerifier.create(likeController.getAllLikesOfUser(
            userId = LikeFixture.like1.userId,
            size = Optional.of(size.toInt()),
            page = Optional.of(index.toInt())
        ))
            .expectNext(expected)
            .verifyComplete()
    }
}
