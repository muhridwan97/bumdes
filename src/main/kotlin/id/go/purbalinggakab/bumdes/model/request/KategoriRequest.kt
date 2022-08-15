package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class KategoriRequest(

    @field:NotBlank
    var nama_kategori: String,
    )

