package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.CreateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateKonsultasiBumdesRequest
import id.go.purbalinggakab.bumdes.model.response.KonsultasiBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.KonsultasiBumdesService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/konsultasi")
class KonsultasiBumdesController(val konsultasiBumdesService: KonsultasiBumdesService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createKonsultasiBumdes(
        @ModelAttribute body: CreateKonsultasiBumdesRequest) : WebResponse<KonsultasiBumdesResponse> {
        val response = konsultasiBumdesService.create(body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listKonsultasiBumdes(
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<KonsultasiBumdesResponse>?> {
        val responses = konsultasiBumdesService.list( request, filter)
        var status = "OK"
        if(responses.items.isEmpty()){
            status = "No Data Available"
        }
        return WebResponse(
            code = 200,
            status = status,
            data = responses
        )
    }

    @GetMapping(
        value = ["/{id}"],
        produces = ["application/json"]
    )
    fun getKonsultasiBumdes(
        @PathVariable("id") id : String) : WebResponse<KonsultasiBumdesResponse>{
        val response = konsultasiBumdesService.get( id)
        return WebResponse(
            200,
            "oke",
            response
        )
    }

    @PatchMapping(
        value = ["/{id}"],
        produces = ["application/json"],
        consumes = ["application/json", MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateKonsultasiBumdes(
        @PathVariable("id") id: String,
        updateKonsultasiBumdesRequest: UpdateKonsultasiBumdesRequest
    ): WebResponse<KonsultasiBumdesResponse>{

        val newsResponse = konsultasiBumdesService.update( id, updateKonsultasiBumdesRequest)
        return WebResponse(
            200,
            "OK",
            newsResponse
        )
    }

    @DeleteMapping(
        value = ["/{id}"],
        produces = ["application/json"]
    )
    fun deleteKonsultasiBumdes(
        @PathVariable("id") id: String,
                       deleteKonsultasiBumdesRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = konsultasiBumdesService.delete( id, deleteKonsultasiBumdesRequest)
        return WebResponse(
            200,
            "OK",
            newsResponse
        )
    }


    @DeleteMapping(
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun deleteListHard(
        @RequestBody ids: List<String>): WebResponse<List<String>> {
        konsultasiBumdesService.deleteList( ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}