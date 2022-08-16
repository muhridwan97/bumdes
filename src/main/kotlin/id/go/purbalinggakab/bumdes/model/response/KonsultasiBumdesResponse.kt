package id.go.purbalinggakab.bumdes.model.response


data class KonsultasiBumdesResponse (
    var id: String,

    var judul : String,

    var deskripsi : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)