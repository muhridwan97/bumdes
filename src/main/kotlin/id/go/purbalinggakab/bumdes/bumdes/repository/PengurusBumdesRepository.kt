package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.PengurusBumdesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PengurusBumdesRepository : JpaRepository<PengurusBumdesEntity, String>, JpaSpecificationExecutor<PengurusBumdesEntity> {

}