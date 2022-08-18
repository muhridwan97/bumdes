package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.CreateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateTanggapanRequest
import id.go.purbalinggakab.bumdes.model.response.TanggapanResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse

interface TanggapanService {
    fun create(createTanggapanRequest: CreateTanggapanRequest) : TanggapanResponse

    fun list(equestParams: RequestParams, filter: Map<String,String>): ListResponse<TanggapanResponse>

    fun get(id: String): TanggapanResponse

    fun update(id: String, updateTanggapanRequest: UpdateTanggapanRequest) : TanggapanResponse

    fun delete(id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(ids : List<String>) : String
}