package id.go.purbalinggakab.bumdes.model.response

data class ItemResponse<T>(

    val id: String?,

    val label: String?,

    val value: T?

)