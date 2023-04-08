package org.hamster.playground.asynchealthcheck.checker.model;

import lombok.extern.slf4j.Slf4j;
import org.hamster.playground.asynchealthcheck.api.model.HealthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
public class ApplicationHealth {

    private final String application;
    private boolean checking;
    private long lastCheckTimeInMs;
    private HealthResult lastResult;
    private long checkIntervalInMs;

    public void updateCheckResult(HealthResult result) {
        if (!Objects.equals(lastResult, result)) {
            log.info("application health {} changed from {} to {}", application, lastResult, result);
        }
        this.checking = false;
        this.lastResult = result;
        this.lastCheckTimeInMs = System.currentTimeMillis();
    }

    public boolean canCheck(long currentTimeMillis) {
        return !checking && currentTimeMillis > lastCheckTimeInMs + checkIntervalInMs;
    }

    public ApplicationHealth(String application, long checkIntervalInMs) {
        this.application = application;
        this.checkIntervalInMs = checkIntervalInMs;
    }

    public String getApplication() {
        return application;
    }

    public long getLastCheckTimeInMs() {
        return lastCheckTimeInMs;
    }


    public HealthResult getLastResult() {
        return lastResult;
    }

    public long getCheckIntervalInMs() {
        return checkIntervalInMs;
    }

    public boolean isChecking() {
        return checking;
    }

    public void setChecking(boolean checking) {
        this.checking = checking;
    }
}
