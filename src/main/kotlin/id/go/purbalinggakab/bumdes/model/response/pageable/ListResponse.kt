package id.go.purbalinggakab.bumdes.model.response.pageable

import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.SortingResponse

data class ListResponse<T>(

    val items: List<T>,

    val paging: PagingResponse? = null,

    val sorting: SortingResponse? = null
)