package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.KecamatanResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.KecamatanService
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/kecamatan")
class KecamatanController(val kecamatanService: KecamatanService) {
    @GetMapping
    fun list(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>): WebResponse<ListResponse<KecamatanResponse>> {
        val result = kecamatanService.list(principal, request, filter)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): WebResponse<KecamatanResponse> {
        val result = kecamatanService.get(id)

        return WebResponse(
            code = 200,
            status = "OK",
            data = result
        )
    }
}