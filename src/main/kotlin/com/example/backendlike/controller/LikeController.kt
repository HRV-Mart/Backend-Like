package com.example.backendlike.controller

import com.example.backendlike.model.Like
import com.example.backendlike.service.LikeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/like")
class LikeController (
    @Autowired
    private val likeService: LikeService
)
{
    @GetMapping("/{userId}")
    fun getAllLikesOfUser(
        @PathVariable userId: String
    ) =
        likeService.getAllLikesOfUser(userId)
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
    fun addProductToLike(@RequestBody like: Like) =
        likeService.addProductToLike(like)
    @DeleteMapping("/{userId}/{productId}")
    fun removeProductFromLike(
        @PathVariable productId: String,
        @PathVariable userId: String
    ) =
        likeService.deleteLike(
            userId = userId,
            productId = productId
        )
}