package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.UnitUsahaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UnitUsahaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface UnitUsahaService {
    fun create(principal: Principal, unitUsahaRequest: UnitUsahaRequest) : UnitUsahaResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<UnitUsahaResponse>

    fun get(principal: Principal, id: String): UnitUsahaResponse

    fun update(principal: Principal, id: String, unitUsahaRequest: UnitUsahaRequest) : UnitUsahaResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}