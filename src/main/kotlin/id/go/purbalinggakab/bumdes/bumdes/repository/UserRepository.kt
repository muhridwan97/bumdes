package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
    @Query(value = "SELECT prv_users.* \n"+
            "FROM prv_users\n"+
            "WHERE prv_users.email = ?#{#email}",

        nativeQuery=true)
    fun getUserByEmail(email : String): UserEntity?

    fun getUserById(id : String) : UserEntity
}