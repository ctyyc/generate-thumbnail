package com.example.demo.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(info = Info(title = "Thumbnail Test API", description = "Thumbnail Test API...", version = "v1"))
@Configuration
class SwaggerConfig {

}