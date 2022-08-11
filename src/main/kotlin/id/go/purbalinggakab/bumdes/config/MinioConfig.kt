package id.go.purbalinggakab.bumdes.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class MinioConfiguration {
    @Value("\${minio.access.key}")
    private val accessKey: String? = null

    @Value("\${minio.access.secret}")
    private val secretKey: String? = null

    @Value("\${minio.url}")
    private val minioUrl: String? = null
    @Bean
    @Primary
    fun minioClient(): MinioClient {
        return MinioClient.Builder()
            .credentials(accessKey, secretKey)
            .endpoint(minioUrl)
            .build()
    }
}