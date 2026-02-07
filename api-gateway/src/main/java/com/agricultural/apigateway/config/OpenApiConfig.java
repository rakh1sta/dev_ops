package com.agricultural.apigateway.config;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties configProps = new SwaggerUiConfigProperties(new PropertyResolverUtils());
        
        // Get all routes and add them as Swagger docs
        Flux<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions();
        List<SwaggerUiConfigProperties.SwaggerUrl> urls = new ArrayList<>();
        
        definitions.toIterable().forEach(routeDefinition -> {
            String id = routeDefinition.getId();
            // Skip internal routes
            if (!id.contains("Refresh") && !id.equals("discoveryLocator")) {
                String swaggerUrl = "/" + id + "/v3/api-docs";
                urls.add(createSwaggerUrl(id, swaggerUrl, id + " API"));
            }
        });
        
        configProps.setUrls(urls);
        return configProps;
    }

    private SwaggerUiConfigProperties.SwaggerUrl createSwaggerUrl(String name, String url, String description) {
        SwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new SwaggerUiConfigProperties.SwaggerUrl();
        swaggerUrl.setName(name);
        swaggerUrl.setUrl(url);
        swaggerUrl.setDescription(description);
        return swaggerUrl;
    }
}