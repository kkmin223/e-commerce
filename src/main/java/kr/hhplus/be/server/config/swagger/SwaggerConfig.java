package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
    title = "E-Commerce API",
    version = "1.0",
    description = "E-Commerce API Documentation"))
public class SwaggerConfig {

}
