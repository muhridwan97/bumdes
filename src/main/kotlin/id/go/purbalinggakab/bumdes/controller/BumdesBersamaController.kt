package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.BumdesBersamaRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.BumdesBersamaResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.BumdesBersamaService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/bumdes-bersama")
class BumdesBersamaController(val bumdesBersamaService: BumdesBersamaService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createBumdesBersama(
        principal: Principal,
        @ModelAttribute body: BumdesBersamaRequest) : WebResponse<BumdesBersamaResponse> {
        val response = bumdesBersamaService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listBumdesBersama(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<BumdesBersamaResponse>?> {
        val responses = bumdesBersamaService.list(principal, request, filter)
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
    fun getBumdesBersama(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<BumdesBersamaResponse>{
        val response = bumdesBersamaService.get(principal, id)
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
    fun updateBumdesBersama(
        principal: Principal,
        @PathVariable("id") id: String,
                   bumdesBersamaRequest: BumdesBersamaRequest
    ): WebResponse<BumdesBersamaResponse>{

        val newsResponse = bumdesBersamaService.update(principal, id, bumdesBersamaRequest)
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
    fun deleteBumdesBersama(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteBumdesBersamaRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = bumdesBersamaService.delete(principal, id, deleteBumdesBersamaRequest)
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
        bumdesBersamaService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}