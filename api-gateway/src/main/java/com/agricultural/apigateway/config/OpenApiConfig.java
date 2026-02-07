package com.agricultural.apigateway.config;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    private final RouteDefinitionLocator routeDefinitionLocator;

    public OpenApiConfig(RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties(SwaggerUiConfigProperties configProps) {
        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();
        Set<SwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        if (definitions != null) {
            definitions.forEach(routeDefinition -> {
                String id = routeDefinition.getId();
                if (!id.contains("ReactiveCompositeDiscoveryClient_") && !id.equals("discoveryLocator")) {
                    String swaggerUrl = "/" + id + "/v3/api-docs";
                    urls.add(new SwaggerUiConfigProperties.SwaggerUrl(id, swaggerUrl, id.toUpperCase() + " API"));
                }
            });
        }

        configProps.setUrls(urls);
        return configProps;
    }
}