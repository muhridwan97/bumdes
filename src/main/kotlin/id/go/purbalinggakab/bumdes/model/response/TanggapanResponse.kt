package id.go.purbalinggakab.bumdes.model.response


data class TanggapanResponse (
    var id: String,

    var id_konsultasi : String,

    var judul_konsultasi : String,

    var tipe : String,

    var pesan : String,

    var created_name : String?="",

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)