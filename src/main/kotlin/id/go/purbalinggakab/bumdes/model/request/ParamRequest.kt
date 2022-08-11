package id.go.purbalinggakab.bumdes.model.request

data class ParamRequest(
    val page: Int = 0,

    val limit: Int = 10,

    val sort: String = "created_at",

    val order: String = "desc"
)
