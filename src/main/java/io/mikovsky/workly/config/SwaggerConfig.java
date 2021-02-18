package io.mikovsky.workly.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;

@Slf4j
@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SwaggerProperties properties;

    @Bean
    public Docket authApi() {
        return baseDocket()
                .groupName("Workly Auth API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.mikovsky.workly.web.auth"))
                .apis(Predicate.not(RequestHandlerSelectors.withClassAnnotation(SwaggerIgnore.class)))
                .build();
    }

    @Bean
    public Docket worklyApiV1() {
        return baseDocket()
                .groupName("Workly API - v1")
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.mikovsky.workly.web.v1"))
                .apis(Predicate.not(RequestHandlerSelectors.withClassAnnotation(SwaggerIgnore.class)))
                .build();
    }

    private Docket baseDocket() {
        Contact contact = new Contact(properties.getContactName(), properties.getContactUrl(), properties.getContactEmail());

        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(properties.getApiInfoTitle())
                .description(properties.getApiInfoDescription())
                .version(properties.getApiInfoVersion())
                .contact(contact)
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(new ApiKey("JWT", "Authorization", "header")))
                .useDefaultResponseMessages(false);
    }

    private SecurityContext securityContext() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return SecurityContext.builder()
                .securityReferences(List.of(new SecurityReference("JWT", authorizationScopes)))
                .forPaths(regex("/api/.*"))
                .build();
    }

}
