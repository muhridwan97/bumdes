package id.go.purbalinggakab.bumdes.model.request

import org.springframework.web.multipart.MultipartFile

data class UpdateFileRequest (
    @SuppressWarnings("java:S1948")
    var file: MultipartFile? = null,
)
