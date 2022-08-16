package id.go.purbalinggakab.bumdes.model.request

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class BumdesBersamaRequest(

    @field:NotBlank
    var id_bumdes: String,

    @field:NotNull
    var id_desa: Long,

    @field:NotNull
    var id_kecamatan: Long,
    )

