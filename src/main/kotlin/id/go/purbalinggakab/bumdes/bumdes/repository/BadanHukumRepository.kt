package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.BadanHukumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BadanHukumRepository : JpaRepository<BadanHukumEntity, String>, JpaSpecificationExecutor<BadanHukumEntity> {

    fun getBadanHukumByNoBadanHukum(no : String) : BadanHukumEntity?
}