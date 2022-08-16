package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.SekolahBumdesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface SekolahBumdesRepository : JpaRepository<SekolahBumdesEntity, String>, JpaSpecificationExecutor<SekolahBumdesEntity> {

}