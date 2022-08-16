package id.go.purbalinggakab.bumdes.model.request

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class BeritaBumdesRequest(

    @field:NotBlank
    var id_kategori: String,

    @field:NotBlank
    var judul: String,

    @field:NotBlank
    var isi: String,

    @field:NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var tanggal : String,

    @field:NotBlank
    var waktu: String,

    @field:NotBlank
    var penulis: String,

    @SuppressWarnings("java:S1948")
    var file: MultipartFile? = null,

    )

