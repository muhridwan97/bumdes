package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class CreateKonsultasiBumdesRequest(
    @field:NotBlank
    var judul: String,

    @field:NotBlank
    var deskripsi: String,

    @field:NotBlank
    var created_by: String,

    )

