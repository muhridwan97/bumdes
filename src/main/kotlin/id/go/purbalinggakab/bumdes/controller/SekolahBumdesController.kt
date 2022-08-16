package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.SekolahBumdesRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.request.UpdateFileRequest
import id.go.purbalinggakab.bumdes.model.response.SekolahBumdesResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.SekolahBumdesService
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerMapping
import java.io.IOException
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/sekolah-bumdes")
class SekolahBumdesController(val sekolahBumdesService: SekolahBumdesService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createSekolahBumdes(
        principal: Principal,
        @ModelAttribute body: SekolahBumdesRequest) : WebResponse<SekolahBumdesResponse> {
        val response = sekolahBumdesService.create(principal, body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listSekolahBumdes(
        principal: Principal,
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<SekolahBumdesResponse>?> {
        val responses = sekolahBumdesService.list(principal, request, filter)
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
    fun getSekolahBumdes(
        principal: Principal,
        @PathVariable("id") id : String) : WebResponse<SekolahBumdesResponse>{
        val response = sekolahBumdesService.get(principal, id)
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
    fun updateSekolahBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                   sekolahBumdesRequest: SekolahBumdesRequest
    ): WebResponse<SekolahBumdesResponse>{

        val newsResponse = sekolahBumdesService.update(principal, id, sekolahBumdesRequest)
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
    fun deleteSekolahBumdes(
        principal: Principal,
        @PathVariable("id") id: String,
                       deleteSekolahBumdesRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = sekolahBumdesService.delete(principal, id, deleteSekolahBumdesRequest)
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
        sekolahBumdesService.deleteList(principal, ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
    @GetMapping(value = ["/attachment/**"])
    @Throws(IOException::class)
    fun getFile(request: HttpServletRequest): ResponseEntity<Any?>? {
        val pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as String
        val filename = AntPathMatcher().extractPathWithinPattern(pattern, request.servletPath)
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        return ResponseEntity.ok()
            .headers(header)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(IOUtils.toByteArray(sekolahBumdesService.getObject(filename)))
    }

    @PatchMapping(
        value = ["/upload-file/{id}"],
        produces = ["application/json"],
        consumes = ["application/json", MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateFileCarousel(
        principal: Principal,
        @PathVariable("id") id: String,
        @ModelAttribute updateFileRequest: UpdateFileRequest
    ): WebResponse<SekolahBumdesResponse>{

        val carouselResponse = sekolahBumdesService.updateFile(principal, id, updateFileRequest)
        return WebResponse(
            200,
            "OK",
            carouselResponse
        )
    }
}