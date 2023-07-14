package com.example.backendlike.service

import com.hrv.mart.custompageable.model.Pageable
import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class LikeService (
    @Autowired
    private val likeRepository: LikeRepository,
    @Autowired
    private val productRepository: ProductRepository
)
{
    fun getAllLikesOfUser(userId: String, pageRequest: PageRequest) =
        likeRepository.findLikeByUserId(userId, pageRequest.withSort(
            Sort.by(
                Sort.Direction.ASC,
                "productId"
            )
        ))
            .map { it.productId }
            .flatMap { productRepository.getProductByProductId(it, response=null) }
            .collectList()
            .flatMap { likes ->
                likeRepository.countLikeByUserId(userId)
                    .map {totalSize ->
                        Pageable<Product>(
                            data = likes,
                            size = pageRequest.pageSize.toLong(),
                            nextPage = Pageable.getNextPage(
                                pageSize = pageRequest.pageSize.toLong(),
                                page = pageRequest.pageNumber.toLong(),
                                totalSize = totalSize
                            )
                        )
                    }
            }
    fun addProductToLike(like: Like, response: ServerHttpResponse) =
        likeRepository.insert(like)
            .map {
                response.statusCode = HttpStatus.OK
                "Like added successfully"
            }
            .onErrorResume {
                response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                Mono.just("Like already exist")
            }
    fun deleteLike(userId: String, productId: String, response: ServerHttpResponse) =
        likeExistByUserIdAndProductId(
            userId = userId,
            productId = productId
        )
            .flatMap {
                if (it) {
                    likeRepository.deleteByUserIdAndProductId(
                        userId = userId,
                        productId = productId
                    )
                        .then(Mono.just("Like removed successfully"))
                }
                else {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Like not found")
                }
            }
    fun likeExistByUserIdAndProductId(userId: String, productId: String) =
        likeRepository.existsByUserIdAndProductId(
            userId = userId,
            productId = productId
        )
}
