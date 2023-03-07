package com.example.backendlike.service

import com.example.backendlike.fixture.Pageable
import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class LikeService (
    @Autowired
    private val likeRepository: LikeRepository
)
{
    fun getAllLikesOfUser(userId: String, pageRequest: PageRequest) =
        likeRepository.findLikeByUserID(userId, pageRequest)
            .collectList()
            .flatMap { likes ->
                likeRepository.countLikeByUserID(userId)
                    .map {totalSize ->
                        Pageable<Like>(
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
                    likeRepository.deleteByUserIDAndProductId(
                        userId = userId,
                        productId = productId
                    )
                        .map {
                            response.statusCode = HttpStatus.OK
                            "Like removed successfully"
                        }
                }
                else {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Like not found")
                }
            }
    fun likeExistByUserIdAndProductId(userId: String, productId: String) =
        likeRepository.existsByUserIDAndProductId(
            userId = userId,
            productId = productId
        )
}