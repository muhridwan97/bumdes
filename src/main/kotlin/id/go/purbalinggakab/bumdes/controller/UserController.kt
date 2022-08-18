package id.go.purbalinggakab.bumdes.controller

import id.go.purbalinggakab.bumdes.model.request.CreateUserRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UpdateUserRequest
import id.go.purbalinggakab.bumdes.model.response.UserResponse
import id.go.purbalinggakab.bumdes.model.response.WebResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.service.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(val userService: UserService) {
    @PostMapping(
        produces = ["application/json"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun createUser(@ModelAttribute body: CreateUserRequest) : WebResponse<UserResponse> {
        val response = userService.create(body)

        return WebResponse(
            code = 200,
            status = "OK",
            data = response
        )
    }
    @GetMapping(
        produces = ["application/json"]
    )
    fun listUser(
        @Valid request: RequestParams,
        @RequestParam filter: Map<String, String>
    ): WebResponse<ListResponse<UserResponse>?> {
        val responses = userService.list(request, filter)
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
    fun getUser(@PathVariable("id") id : String) : WebResponse<UserResponse>{
        val response = userService.get(id)
        return WebResponse(
            200,
            "oke",
            response
        )
    }


    @GetMapping(
        value = ["/email/{email}"],
        produces = ["application/json"]
    )
    fun getUserByEmail(@PathVariable("email") email : String) : WebResponse<UserResponse>{
        val response = userService.getByEmail(email)
        return WebResponse(
            200,
            "oke",
            response
        )
    }

    @PatchMapping(
        value = ["/{id}"],
        produces = ["application/json"],
        consumes = ["application/json", MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun updateUser(@PathVariable("id") id: String,
                   updateUserRequest: UpdateUserRequest
    ): WebResponse<UserResponse>{

        val newsResponse = userService.update(id, updateUserRequest)
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
    fun deleteUser(@PathVariable("id") id: String,
                       deleteUserRequest: DeleteRequest
    ): WebResponse<String>{

        val newsResponse = userService.delete(id, deleteUserRequest)
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
    fun deleteListHard(@RequestBody ids: List<String>): WebResponse<List<String>> {
        userService.deleteList(ids)
        return WebResponse(
            code = 200,
            status = "OK",
            data = ids
        )
    }
}