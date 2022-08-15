package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.PengurusRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface PengurusService {
    fun create(principal: Principal, pengurusRequest: PengurusRequest) : PengurusResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<PengurusResponse>

    fun get(principal: Principal, id: String): PengurusResponse

    fun update(principal: Principal, id: String, pengurusRequest: PengurusRequest) : PengurusResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}