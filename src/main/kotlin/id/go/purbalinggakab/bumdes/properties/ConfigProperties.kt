package id.go.purbalinggakab.bumdes.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "config")
@ConstructorBinding
data class ConfigProperties(
    var corsAllowed: String?
)
