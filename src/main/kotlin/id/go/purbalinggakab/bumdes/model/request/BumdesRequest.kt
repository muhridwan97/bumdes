package id.go.purbalinggakab.bumdes.model.request

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class BumdesRequest(

    @field:NotNull
    var id_desa: Long,

    @field:NotNull
    var id_kecamatan: Long,

    @field:NotNull
    var id_badan_hukum: String,

    @field:NotNull
    var id_unit_usaha: String,

    @field:NotBlank
    var nama: String,

    @field:NotNull
    var jumlah_kontribusi_pad: Double,

    @field:NotBlank
    var klasifikasi: String,

    @field:NotBlank
    var tipe: String,

    @SuppressWarnings("java:S1948")
    var file: MultipartFile? = null
    )

