package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.BumdesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BumdesRepository : JpaRepository<BumdesEntity, String>, JpaSpecificationExecutor<BumdesEntity> {

}