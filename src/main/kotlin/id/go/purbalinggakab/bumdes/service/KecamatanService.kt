package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.KecamatanRequest
import id.go.purbalinggakab.bumdes.model.response.KecamatanResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import java.security.Principal

interface KecamatanService {
    fun list(principal: Principal, requestParams: RequestParams, filter: Map<String, String>): ListResponse<KecamatanResponse>

    fun get(id: Long): KecamatanResponse

//    fun create(principal: Principal, kecamatanRequest: KecamatanRequest): KecamatanResponse
//
//    fun update(id: String, kecamatanRequest: KecamatanRequest): KecamatanResponse
//
//    fun delete(id: String)
//
//    fun deleteList(ids: List<String>): String
}