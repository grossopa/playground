package org.hamster.playground.asynchealthcheck.web.router;

import org.hamster.playground.asynchealthcheck.web.handler.HealthCheckHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class HealthCheckRouter {

    @Bean
    public RouterFunction<ServerResponse> route(HealthCheckHandler healthCheckHandler) {
        return RouterFunctions.route(GET("/health-check").and(accept(APPLICATION_JSON)),
                healthCheckHandler::healthCheck);
    }
}
