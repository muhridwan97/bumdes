package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PengurusRequest(

    @field:NotBlank
    var nama_pengurus: String,
    )

