package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.DesaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface DesaRepository : JpaRepository<DesaEntity, String>, JpaSpecificationExecutor<DesaEntity> {

}