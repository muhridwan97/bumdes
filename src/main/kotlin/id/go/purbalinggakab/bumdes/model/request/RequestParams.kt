package id.go.purbalinggakab.bumdes.model.request

data class RequestParams(

    val page: Int? = 0,

    val size: Int? = 20,

    val sortBy: String? = "created_at:ASC"

)