package id.go.purbalinggakab.bumdes.bumdes

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["id.go.purbalinggakab.bumdes.bumdes"],
    entityManagerFactoryRef = "bumdesEntityManager",
    transactionManagerRef = "bumdesTransactionManager"
)
class BumdesDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-bumdes")
    fun bumdesDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    fun bumdesEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = bumdesDataSource()
            setPackagesToScan("id.go.purbalinggakab.bumdes.bumdes")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun bumdesTransactionManager() = JpaTransactionManager(bumdesEntityManager().`object`!!)
}