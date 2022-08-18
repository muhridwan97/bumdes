package id.go.purbalinggakab.bumdes.model.request

import javax.validation.constraints.NotBlank

data class UpdateTanggapanRequest(
    @field:NotBlank
    var id_konsultasi: String,

    @field:NotBlank
    var tipe: String,

    @field:NotBlank
    var pesan: String,

    @field:NotBlank
    var updated_by: String,

    )

