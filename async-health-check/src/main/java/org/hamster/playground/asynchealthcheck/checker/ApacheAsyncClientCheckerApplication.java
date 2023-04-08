package org.hamster.playground.asynchealthcheck.checker;

import org.hamster.playground.asynchealthcheck.checker.action.HealthChecker;
import org.hamster.playground.asynchealthcheck.checker.action.HttpAsyncClientHealthChecker;
import org.hamster.playground.asynchealthcheck.checker.action.HttpClientHealthChecker;
import org.hamster.playground.asynchealthcheck.checker.model.ApplicationHealth;

/**
 * @author Jack Yin
 * @since 1.0
 */
public class ApacheAsyncClientCheckerApplication {

    public static void main(String[] args) {
        // HealthChecker checker = new HttpAsyncClientHealthChecker();
        HealthChecker checker = new HttpClientHealthChecker();
        for (int i = 0; i < 500; i++) {
            checker.addApplication(new ApplicationHealth("some-service-" + (i + 1000), 5000L));
        }
        System.out.println("Start to check");
        checker.checkAll();
    }
}
