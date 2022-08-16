package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.PengurusBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface PengurusBumdesService {
    fun create(principal: Principal, pengurusBumdesRequest: PengurusBumdesRequest) : PengurusBumdesResponse

    fun list(principal: Principal,requestParams: RequestParams, filter: Map<String,String>): ListResponse<PengurusBumdesResponse>

    fun get(principal: Principal, id: String): PengurusBumdesResponse

    fun update(principal: Principal, id: String, pengurusBumdesRequest: PengurusBumdesRequest) : PengurusBumdesResponse

    fun delete(principal: Principal, id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(principal: Principal, ids : List<String>) : String
}