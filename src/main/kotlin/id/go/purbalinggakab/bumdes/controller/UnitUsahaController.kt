package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.UnitUsahaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UnitUsahaResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.UnitUsahaService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/unit-usaha")
class UnitUsahaController(val unitUsahaService: UnitUsahaService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createUnitUsaha(
        principal: Principal,
        @ModelAttribute body: UnitUsahaRequest) : WebResponse<UnitUsahaResponse> {
        val response = unitUsahaService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listUnitUsaha(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<UnitUsahaResponse>?> {
        val responses = unitUsahaService.list(principal, request, filter)
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
    fun getUnitUsaha(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<UnitUsahaResponse>{
        val response = unitUsahaService.get(principal, id)
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
    fun updateUnitUsaha(
        principal: Principal,
        @PathVariable("id") id: String,
                   unitUsahaRequest: UnitUsahaRequest
    ): WebResponse<UnitUsahaResponse>{

        val newsResponse = unitUsahaService.update(principal, id, unitUsahaRequest)
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
    fun deleteUnitUsaha(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteUnitUsahaRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = unitUsahaService.delete(principal, id, deleteUnitUsahaRequest)
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
        unitUsahaService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}