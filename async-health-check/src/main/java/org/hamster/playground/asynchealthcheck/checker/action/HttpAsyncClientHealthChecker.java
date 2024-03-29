package org.hamster.playground.asynchealthcheck.checker.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.hamster.playground.asynchealthcheck.api.model.HealthResult;
import org.hamster.playground.asynchealthcheck.checker.model.ApplicationHealth;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
public class HttpAsyncClientHealthChecker implements HealthChecker {

    private final List<ApplicationHealth> cache = new CopyOnWriteArrayList<>();
    private final CloseableHttpAsyncClient client;

    public HttpAsyncClientHealthChecker() {
        client = HttpAsyncClientBuilder.create().setMaxConnPerRoute(1000).setMaxConnTotal(1000).build();
    }

    @Override
    @SneakyThrows
    public void checkAll() {
        client.start();
        while (true) {
            Thread.sleep(200L);
            log.info("Check {} applications health status.", cache.size());
            for (ApplicationHealth health : cache) {
                if (health.canCheck(System.currentTimeMillis())) {
                    check(health);
                    // do we need to pause here?
                }
            }
        }
    }

    private void check(ApplicationHealth health) {
        HttpGet request = new HttpGet(
                "http://localhost:19080/health-check?delayInMs=1&application=" + health.getApplication());

        health.setChecking(true);
        client.execute(request, new FutureCallback<>() {
            @Override
            @SneakyThrows
            public void completed(HttpResponse httpResponse) {
                HealthResult result = new ObjectMapper().readValue(httpResponse.getEntity().getContent(),
                        HealthResult.class);
                health.updateCheckResult(result);
                log.info("Health Check is done for application {}", health.getApplication());
            }

            @Override
            public void failed(Exception e) {
                health.updateCheckResult(null);
                log.error(e.getMessage(), e);
            }

            @Override
            public void cancelled() {
                health.updateCheckResult(null);
            }
        });
    }

    @Override
    public boolean addApplication(ApplicationHealth applicationHealth) {
        cache.add(applicationHealth);
        return true;
    }

    @Override
    public ApplicationHealth removeApplication(String application) {
        return null;
    }

}
