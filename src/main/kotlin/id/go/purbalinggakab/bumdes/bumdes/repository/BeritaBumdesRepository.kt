package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.BeritaBumdesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BeritaBumdesRepository : JpaRepository<BeritaBumdesEntity, String>, JpaSpecificationExecutor<BeritaBumdesEntity> {

}