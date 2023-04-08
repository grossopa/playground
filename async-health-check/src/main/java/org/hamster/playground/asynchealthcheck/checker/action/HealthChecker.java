package org.hamster.playground.asynchealthcheck.checker.action;

import org.hamster.playground.asynchealthcheck.checker.model.ApplicationHealth;

/**
 * @author Jack Yin
 * @since 1.0
 */
public interface HealthChecker {

    void checkAll();

    boolean addApplication(ApplicationHealth applicationHealth);

    ApplicationHealth removeApplication(String application);

}
