package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UnitUsahaRequest(

    @field:NotBlank
    var nama_unit_usaha: String,

    @field:NotBlank
    var kerja_sama: String,
    )

