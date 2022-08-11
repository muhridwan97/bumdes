package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class DesaRequest(

    @field:NotBlank
    var nama_desa: String,
    )

