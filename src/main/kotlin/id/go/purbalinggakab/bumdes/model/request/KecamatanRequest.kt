package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class KecamatanRequest(

    @field:NotBlank
    var nama_kecamatan: String,
    )

