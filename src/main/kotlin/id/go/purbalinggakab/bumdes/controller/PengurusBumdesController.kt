package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.PengurusBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.PengurusBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.PengurusBumdesService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/pengurus-bumdes")
class PengurusBumdesController(val pengurusBumdesService: PengurusBumdesService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createPengurusBumdes(
        principal: Principal,
        @ModelAttribute body: PengurusBumdesRequest) : WebResponse<PengurusBumdesResponse> {
        val response = pengurusBumdesService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listPengurusBumdes(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<PengurusBumdesResponse>?> {
        val responses = pengurusBumdesService.list(principal, request, filter)
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
    fun getPengurusBumdes(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<PengurusBumdesResponse>{
        val response = pengurusBumdesService.get(principal, id)
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
    fun updatePengurusBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                   pengurusBumdesRequest: PengurusBumdesRequest
    ): WebResponse<PengurusBumdesResponse>{

        val newsResponse = pengurusBumdesService.update(principal, id, pengurusBumdesRequest)
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
    fun deletePengurusBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                       deletePengurusBumdesRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = pengurusBumdesService.delete(principal, id, deletePengurusBumdesRequest)
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
        principal: Principal,
        @RequestBody ids: List<String>): WebResponse<List<String>> {
        pengurusBumdesService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}