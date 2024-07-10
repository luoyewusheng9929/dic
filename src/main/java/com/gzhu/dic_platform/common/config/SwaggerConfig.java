package com.gzhu.dic_platform.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile({"dev", "test"})
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Swagger 测试")
                        .description("Swagger 测试文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .url("https://www.example.com")
                                .email("developer@example.com")));

        addSecurity(openAPI);

        return openAPI;
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/public/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    private SecurityRequirement securityItem() {
        return new SecurityRequirement().addList("token");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("token")
                .type(Type.APIKEY)
                .in(In.HEADER);
    }

    private void addSecurity(OpenAPI openAPI) {
        openAPI.addSecurityItem(securityItem())
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("token", securityScheme()));
    }
}