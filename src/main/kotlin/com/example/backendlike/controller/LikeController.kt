package com.example.backendlike.controller

import com.hrv.mart.custompageable.CustomPageRequest
import com.example.backendlike.model.Like
import com.example.backendlike.service.LikeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/like")
class LikeController (
    @Autowired
    private val likeService: LikeService
)
{
    @GetMapping("/{userId}")
    fun getAllLikesOfUser(
        @PathVariable userId: String,
        @RequestParam size: Optional<Int>,
        @RequestParam page: Optional<Int>
    ) =
        likeService.getAllLikesOfUser(
            userId,
            CustomPageRequest.getPageRequest(
                optionalSize = size,
                optionalPage = page
            )
        )
    @GetMapping("/{userId}/{productId}")
    fun isProductLikedByUser(
        @PathVariable productId: String,
        @PathVariable userId: String
    ) =
        likeService.likeExistByUserIdAndProductId(
            userId = userId,
            productId = productId
        )
    @PostMapping()
    fun addProductToLike(
        @RequestBody like: Like,
        response: ServerHttpResponse
    ) =
        likeService.addProductToLike(like, response)
    @DeleteMapping("/{userId}/{productId}")
    fun removeProductFromLike(
        @PathVariable productId: String,
        @PathVariable userId: String,
        response: ServerHttpResponse
    ) =
        likeService.deleteLike(
            userId = userId,
            productId = productId,
            response = response
        )
}