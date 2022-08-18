package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class CreateTanggapanRequest(
    @field:NotBlank
    var id_konsultasi: String,

    @field:NotBlank
    var tipe: String,

    @field:NotBlank
    var pesan: String,

    @field:NotBlank
    var created_by: String,

    )

