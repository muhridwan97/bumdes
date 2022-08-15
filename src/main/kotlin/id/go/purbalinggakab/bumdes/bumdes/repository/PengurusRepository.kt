package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.PengurusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PengurusRepository : JpaRepository<PengurusEntity, String>, JpaSpecificationExecutor<PengurusEntity> {

}