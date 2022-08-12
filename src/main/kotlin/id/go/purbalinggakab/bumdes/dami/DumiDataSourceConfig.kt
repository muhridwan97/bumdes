package id.go.purbalinggakab.bumdes.dami

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
    basePackages = ["id.go.purbalinggakab.bumdes.dami"],
    entityManagerFactoryRef = "damiEntityManager",
    transactionManagerRef = "damiTransactionManager"
)
class DumiDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-dami")
    fun damiDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    fun damiEntityManager(): LocalContainerEntityManagerFactoryBean =
        (LocalContainerEntityManagerFactoryBean()).apply {
            dataSource = damiDataSource()
            setPackagesToScan("id.go.purbalinggakab.bumdes.dami")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
        }

    @Bean
    fun damiTransactionManager() = JpaTransactionManager(damiEntityManager().`object`!!)
}