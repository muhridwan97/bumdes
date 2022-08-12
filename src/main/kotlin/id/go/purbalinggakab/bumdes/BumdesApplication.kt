package id.go.purbalinggakab.bumdes

import id.go.purbalinggakab.bumdes.properties.ConfigProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties::class)
class BumdesApplication

fun main(args: Array<String>) {
	runApplication<BumdesApplication>(*args)
}
