package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class BadanHukumRequest(

    @field:NotNull
    var id_desa: Long,

    @field:NotNull
    var id_kecamatan: Long,

    @field:NotBlank
    var no_badan_hukum: String,

    @field:NotBlank
    var nama_bumdes: String,
    )

