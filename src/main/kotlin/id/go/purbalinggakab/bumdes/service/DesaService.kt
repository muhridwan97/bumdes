package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.DesaResponse
import id.go.purbalinggakab.bumdes.model.response.ItemResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface DesaService {
    fun list(principal: Principal, requestParams: RequestParams, filter: Map<String, String>): ListResponse<DesaResponse>

    fun get(id: Long): DesaResponse

    fun listAll(): List<ItemResponse<String>>

    fun listByKec(idKec : Long): List<ItemResponse<String>>
}