package id.go.purbalinggakab.bumdes.model.response


data class UnitUsahaResponse (
    var id: String,

    var nama_unit_usaha : String,

    var kerja_sama : String,

    var created_at : String,

    val created_by: String,

    val updated_at: String?,

    val updated_by: String?,

    val deleted_at: String?,

    val deleted_by: String?,
)