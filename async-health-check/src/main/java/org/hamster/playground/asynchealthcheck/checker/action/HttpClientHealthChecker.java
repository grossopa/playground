package org.hamster.playground.asynchealthcheck.checker.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamster.playground.asynchealthcheck.api.model.HealthResult;
import org.hamster.playground.asynchealthcheck.checker.model.ApplicationHealth;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
public class HttpClientHealthChecker implements HealthChecker {

    private final List<ApplicationHealth> cache = new CopyOnWriteArrayList<>();
    private final CloseableHttpClient client;
    Executor executor = Executors.newFixedThreadPool(50);

    public HttpClientHealthChecker() {
        client = HttpClientBuilder.create().setMaxConnPerRoute(1000).setMaxConnTotal(1000).build();
    }

    @Override
    @SneakyThrows
    public void checkAll() {
        while (true) {
            Thread.sleep(200L);
            log.info("Check {} applications health status.", cache.size());
            for (ApplicationHealth health : cache) {
                if (health.canCheck(System.currentTimeMillis())) {
                    check(health);
                }
            }
        }
    }

    private void check(ApplicationHealth health) {


        executor.execute(() -> {
            HttpGet request = new HttpGet(
                    "http://localhost:19080/health-check?delayInMs=1&application=" + health.getApplication());
            health.setChecking(true);
            try {
                HttpResponse httpResponse = client.execute(request);
                HealthResult result = new ObjectMapper().readValue(httpResponse.getEntity().getContent(),
                        HealthResult.class);
                health.updateCheckResult(result);
                log.info("Health Check is done for application {}", health.getApplication());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
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
