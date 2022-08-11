package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.DesaRequest
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface DesaService {
    fun list(principal: Principal, requestParams: RequestParams, filter: Map<String, String>): ListResponse<DesaResponse>

    fun get(id: String): DesaResponse

    fun create(principal: Principal, desaRequest: DesaRequest): DesaResponse

    fun update(id: String, desaRequest: DesaRequest): DesaResponse

    fun delete(id: String)

    fun deleteList(ids: List<String>): String
}