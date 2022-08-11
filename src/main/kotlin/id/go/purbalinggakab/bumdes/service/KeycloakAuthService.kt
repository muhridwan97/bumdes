package id.go.purbalinggakab.bumdes.service

import id.go.purbalinggakab.bumdes.model.response.UserInfoResponse
import java.security.Principal

interface KeycloakAuthService {

    fun getPreferredUsername(principal: Principal): String?

    fun getLevel(principal: Principal): MutableSet<String>?

    fun getUserInfo(principal: Principal): UserInfoResponse

}