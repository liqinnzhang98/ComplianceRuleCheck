package com.apromore.compliance_centre.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi complianceCentreApi() {
        return GroupedOpenApi.builder().group("http").pathsToMatch("/**").build();
    }

    @SuppressWarnings("unused")
    private OpenAPI apiInfo() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Apromore Compliance Centre REST API")
                    .description("The REST API for interfacing with Apromore's compliance management services")
                    .version("v1")
            );
    }
}
