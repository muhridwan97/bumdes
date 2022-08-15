package id.go.purbalinggakab.bumdes.model.response


data class PengurusResponse (
    var id: String,

    var nama_pengurus : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)