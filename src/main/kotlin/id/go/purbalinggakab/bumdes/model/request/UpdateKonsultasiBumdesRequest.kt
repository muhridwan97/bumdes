package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class UpdateKonsultasiBumdesRequest(
    @field:NotBlank
    var judul: String,

    @field:NotBlank
    var deskripsi: String,

    @field:NotBlank
    var updated_by: String,

    )

