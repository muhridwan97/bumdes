package id.go.purbalinggakab.bumdes.model.response.pageable

data class PagingResponse(

    val item_per_page: Int,

    val page: Int,

    val total_item: Long,

    val total_page: Int

)