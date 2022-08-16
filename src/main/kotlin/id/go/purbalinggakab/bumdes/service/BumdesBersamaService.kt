package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.BumdesBersamaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BumdesBersamaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface BumdesBersamaService {
    fun create(principal: Principal, bumdesBersamaRequest: BumdesBersamaRequest) : BumdesBersamaResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<BumdesBersamaResponse>

    fun get(principal: Principal, id: String): BumdesBersamaResponse

    fun update(principal: Principal, id: String, bumdesBersamaRequest: BumdesBersamaRequest) : BumdesBersamaResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}