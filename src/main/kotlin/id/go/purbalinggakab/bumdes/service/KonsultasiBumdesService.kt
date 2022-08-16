package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.request.CreateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.response.KonsultasiBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse

interface KonsultasiBumdesService {
    fun create(createKonsultasiBumdesRequest: CreateKonsultasiBumdesRequest) : KonsultasiBumdesResponse

    fun list(equestParams: RequestParams, filter: Map<String,String>): ListResponse<KonsultasiBumdesResponse>

    fun get(id: String): KonsultasiBumdesResponse

    fun update(id: String, updateKonsultasiBumdesRequest: UpdateKonsultasiBumdesRequest) : KonsultasiBumdesResponse

    fun delete(id: String, deleteRequest: DeleteRequest) : String

    fun deleteList(ids : List<String>) : String
}