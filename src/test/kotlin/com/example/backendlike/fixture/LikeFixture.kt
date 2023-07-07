package com.example.backendlike.fixture

import com.example.backendlike.model.Like
import com.hrv.mart.product.model.Product

object LikeFixture {
    val like1 = Like(
        userId = "userID",
        productId = "test_image_1"
    )
    val like2 = Like(
        userId = "userID",
        productId = "test_image_2"
    )
    val product1 = Product(
        name = "Test Product",
        description = "Only for testing",
        images = listOf("https://image.hrv-mart.com/test_image_1"),
        price = 100,
        id = "test_image_1"
    )
    val product2 = Product(
        name = "Test Product 2",
        description = "Only for testing",
        images = listOf("https://image.hrv-mart.com/test_image_2"),
        price = 100,
        id = "test_image_2"
    )
}