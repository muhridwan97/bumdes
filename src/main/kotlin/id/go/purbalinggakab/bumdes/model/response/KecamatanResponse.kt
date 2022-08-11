package id.go.purbalinggakab.bumdes.model.response


data class KecamatanResponse (
    var id: String,

    var nama_kecamatan: String,

    var is_deleted : Boolean? = false,

    var created_at : String,

    var created_by : String,

    var updated_at : String?,

    var updated_by : String?,

    var deleted_at : String?,

    var deleted_by : String?
)