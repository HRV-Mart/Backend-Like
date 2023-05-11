package com.example.backendlike.config

import com.hrv.mart.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ProductConfiguration (
    @Autowired
    private val webClientBuilder: WebClient.Builder,
)
{
    @Bean
    fun getProductRepository() =
        ProductRepository(webClientBuilder)

}