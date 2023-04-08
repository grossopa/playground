package org.hamster.playground.asynchealthcheck.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.hamster.playground.asynchealthcheck.api.model.HealthResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class HealthCheckHandler {

    public Mono<ServerResponse> healthCheck(ServerRequest request) {
        long delayInMs = parseLong(request.queryParam("delayInMs").orElse("1"));
        String application = request.queryParam("application").orElseThrow();

        log.info("Invoked application {} with delay {}ms", application, delayInMs);

        return ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(
                        new HealthResult("OK", format("Application %s run normally", application))))
                .delayElement(ofMillis(delayInMs));
    }
}
