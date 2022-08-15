package id.go.purbalinggakab.bumdes.bumdes.repository

import id.go.purbalinggakab.bumdes.bumdes.entity.KategoriEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface KategoriRepository : JpaRepository<KategoriEntity, String>, JpaSpecificationExecutor<KategoriEntity> {

}