package com.example.backendlike.service

import com.example.backendlike.model.Like
import com.example.backendlike.repository.LikeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LikeService (
    @Autowired
    private val likeRepository: LikeRepository
)
{
    fun getAllLikesOfUser(userId: String) =
        likeRepository.findLikeByUserID(userId)
    fun addProductToLike(like: Like) =
        likeRepository.insert(like)
    fun deleteLike(userId: String, productId: String) =
        likeRepository.deleteByUserIDAndProductId(
            userId = userId,
            productId = productId
        )
    fun likeExistByUserIdAndProductId(userId: String, productId: String) =
        likeRepository.existsByUserIDAndProductId(
            userId = userId,
            productId = productId
        )
}