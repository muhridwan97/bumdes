package id.go.purbalinggakab.bumdes.model.response


data class PengurusBumdesResponse (
    var id: String,

    var id_bumdes : String,

    var nama_bumdes : String,

    var id_pengurus : String,

    var nama_pengurus : String,

    var jabatan : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)