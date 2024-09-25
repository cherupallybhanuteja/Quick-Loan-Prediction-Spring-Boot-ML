package com.gonnect.deeplearning.loanapprover.configurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;

@Configuration
public class AppConfigurer {

    @Bean
    public GroupedOpenApi api3() {
        return GroupedOpenApi.builder()
                .group("api-3")  // Yet another unique group name
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi api4() {
        return GroupedOpenApi.builder()
                .group("api-4")  // Unique group name
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Approver API")
                        .version("v1")
                        .description("API documentation for Loan Approver System"));
    }
}
