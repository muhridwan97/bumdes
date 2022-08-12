//package id.go.purbalinggakab.bumdes.config
//
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.context.properties.ConfigurationProperties
//import org.springframework.boot.jdbc.DataSourceBuilder
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.orm.jpa.JpaTransactionManager
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
//import org.springframework.transaction.PlatformTransactionManager
//import org.springframework.transaction.annotation.EnableTransactionManagement
//import javax.persistence.EntityManagerFactory
//import javax.sql.DataSource
//
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    entityManagerFactoryRef = "dumiEntityManagerFactory",
//    transactionManagerRef = "dumiTransactionManager",
//    basePackages = ["id.go.purbalinggakab.bumdes.repository"]
//)
//class BookDBConfig {
//    @Bean(name = ["dumiDataSource"])
//    @ConfigurationProperties(prefix = "spring.datasource-dumi")
//    fun dataSource(): DataSource {
//        return DataSourceBuilder.create().build()
//    }
//
//    @Bean(name = ["dumiEntityManagerFactory"])
//    fun dumiEntityManagerFactory(
//        builder: EntityManagerFactoryBuilder,
//        @Qualifier("dumiDataSource") dataSource: DataSource?
//    ): LocalContainerEntityManagerFactoryBean {
//        val properties: HashMap<String, Any> = HashMap()
////        properties["hibernate.hbm2ddl.auto"] = "update"
////        properties["hibernate.dialect"] = "org.hibernate.dialect.Oracle10gDialect"
//        return builder.dataSource(dataSource).properties(properties)
//            .packages("id.go.purbalinggakab.bumdes.entity").persistenceUnit("KecamatanEntity").build()
//    }
//
//    @Bean(name = ["dumiTransactionManager"])
//    fun dumiTransactionManager(
//        @Qualifier("dumiEntityManagerFactory") dumiEntityManagerFactory: EntityManagerFactory?
//    ): PlatformTransactionManager {
//        return JpaTransactionManager(dumiEntityManagerFactory!!)
//    }
//}