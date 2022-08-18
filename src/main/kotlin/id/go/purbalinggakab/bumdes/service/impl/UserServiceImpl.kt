package id.go.purbalinggakab.bumdes.service.impl

import id.go.purbalinggakab.bumdes.bumdes.entity.UserEntity
import id.go.purbalinggakab.bumdes.bumdes.repository.UserRepository
import id.go.purbalinggakab.bumdes.error.DeleteDataException
import id.go.purbalinggakab.bumdes.error.NotFoundException
import id.go.purbalinggakab.bumdes.extensions.formateDateTime
import id.go.purbalinggakab.bumdes.model.request.CreateUserRequest
import id.go.purbalinggakab.bumdes.model.request.DeleteRequest
import id.go.purbalinggakab.bumdes.model.request.RequestParams
import id.go.purbalinggakab.bumdes.model.response.UpdateUserRequest
import id.go.purbalinggakab.bumdes.model.response.UserResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.ListResponse
import id.go.purbalinggakab.bumdes.model.response.pageable.PagingResponse
import id.go.purbalinggakab.bumdes.service.UserService
import id.go.purbalinggakab.bumdes.specification.FilterMapper
import id.go.purbalinggakab.bumdes.specification.FilterRequestUtil
import id.go.purbalinggakab.bumdes.specification.FilterSpecification
import id.go.purbalinggakab.bumdes.validation.ValidationUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class UserServiceImpl (
    val userRepository: UserRepository,
    val validationUtil: ValidationUtil,
    val filterRequestUtil: FilterRequestUtil,
    private val specification: FilterSpecification<UserEntity>
        ): UserService {

    override fun create(createUserRequest: CreateUserRequest): UserResponse {
        validationUtil.validate(createUserRequest)

        val userData = userRepository.getUserByEmail(createUserRequest.email)

        if(userData == null){
            val userEntity = UserEntity(
                id = "",
                googleId = createUserRequest.googleId,
                token = createUserRequest.token!!,
                email = createUserRequest.email,
                displayName = createUserRequest.displayName!!,
                givenName = createUserRequest.givenName!!,
                familyName = createUserRequest.familyName!!,
                photo = createUserRequest.photo!!,
                createdAt = Date(),
                createdBy = createUserRequest.createdBy!!,
                updatedAt = null,
                updatedBy = null,
                deletedAt = null,
                deletedBy = null
            )

            val result = userRepository.save(userEntity)

            return convertToResponse(result)
        }
        userData.apply {
            googleId = createUserRequest.googleId
            token = createUserRequest.token!!
            email = createUserRequest.email
            displayName = createUserRequest.displayName!!
            givenName = createUserRequest.givenName!!
            familyName = createUserRequest.familyName!!
            photo = createUserRequest.photo!!
            updatedBy = createUserRequest.createdBy!!
            updatedAt = Date()
        }
        userRepository.save(userData)
        return convertToResponse(userData)

    }

    override fun list(requestParams: RequestParams, filter: Map<String, String>): ListResponse<UserResponse> {
        val size = if (requestParams.size!! == -1) {
            Integer.MAX_VALUE
        } else {
            requestParams.size
        }

        val pageable = PageRequest.of(
            requestParams.page!!,
            size,
            filterRequestUtil.toSortBy(requestParams.sortBy!!)
        )

        val list = userRepository.findAll(generateFilter(filter), pageable)

        val items: List<UserEntity> = list.get().collect(Collectors.toList())

        return ListResponse(
            items = items.map { convertToResponse(it) },
            paging = PagingResponse(
                item_per_page = list.size,
                page = list.number,
                total_item = list.totalElements,
                total_page = list.totalPages
            ),
            sorting = filterRequestUtil.toSortResponse(requestParams.sortBy)
        )
    }

    override fun get(id: String): UserResponse {
        val response = findUserByIdOrThrowNotFound(id)
        return convertToResponse(response)
    }

    override fun getByEmail(email: String): UserResponse {
        val response = userRepository.getUserByEmail(email)

        if (response == null){
            throw NotFoundException()
        }else{
            return convertToResponse(response)
        }
    }

    override fun update(id: String, updateUserRequest: UpdateUserRequest): UserResponse {
        val result = findUserByIdOrThrowNotFound(id)
        validationUtil.validate(result)

        result.apply {
            googleId = updateUserRequest.googleId
            token = updateUserRequest.token!!
            email = updateUserRequest.email
            displayName = updateUserRequest.displayName!!
            givenName = updateUserRequest.givenName!!
            familyName = updateUserRequest.familyName!!
            photo = updateUserRequest.photo!!
            updatedBy = updateUserRequest.updatedBy!!
            updatedAt = Date()
        }
        userRepository.save(result)
        return convertToResponse(result)
    }

    override fun delete(id: String, deleteUserRequest: DeleteRequest): String {
        val user = findUserByIdOrThrowNotFound(id)

        try {
            if(deleteUserRequest.softDelete == true || deleteUserRequest.softDelete == null){
                user.apply {
                    isDeleted = true
                    deletedBy = ""
                    deletedAt = Date()
                }
                userRepository.save(user)
            }else{
                userRepository.delete(user)
            }
        } catch (e: Exception) {
            println("error -> $e")
            throw DeleteDataException()
        }
        return "Delete Successfully"
    }

    override fun deleteList(ids: List<String>): String {
        userRepository.deleteAllById(ids)
        return "Delete Successfully"
    }

    private fun convertToResponse(userEntity: UserEntity): UserResponse {
        return UserResponse(
            id = userEntity.id,
            googleId = userEntity.googleId,
            token = userEntity.token,
            email = userEntity.email,
            displayName = userEntity.displayName,
            givenName = userEntity.givenName,
            familyName = userEntity.familyName,
            photo = userEntity.photo,
            isDeleted = userEntity.isDeleted,
            createdAt = userEntity.createdAt.formateDateTime(),
            createdBy = userEntity.createdBy,
            updatedAt = if (userEntity.updatedAt == null) null else userEntity.updatedAt!!.formateDateTime(),
            updatedBy = userEntity.updatedBy,
            deletedAt = if (userEntity.deletedAt == null) null else userEntity.deletedAt!!.formateDateTime(),
            deletedBy = userEntity.deletedBy,
        )
    }

    private fun generateFilter(filter: Map<String, String>): Specification<UserEntity>? {
        val options: MutableList<FilterMapper> = mutableListOf()
        val filters = filterRequestUtil.toFilterCriteria(filter, options)
        return specification.buildPredicate(filters)
    }

    private fun findUserByIdOrThrowNotFound(id: String): UserEntity{
        val result = userRepository.findByIdOrNull(id)
        if (result == null){
            throw NotFoundException()
        }else{
            return result
        }
    }
}