package org.hamster.playground.asynchealthcheck.checker;

import org.apache.http.nio.reactor.IOReactorException;
import org.hamster.playground.asynchealthcheck.checker.action.HttpAsyncClientHealthChecker;
import org.hamster.playground.asynchealthcheck.checker.model.ApplicationHealth;

/**
 * @author Jack Yin
 * @since 1.0
 */
public class ApacheAsyncClientCheckerApplication {

    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public static void main(String[] args) throws IOReactorException {
        HttpAsyncClientHealthChecker checker = new HttpAsyncClientHealthChecker();
        for (int i = 0; i < 500; i++) {
            checker.addApplication(new ApplicationHealth("some-service-" + (i + 1000), 5000L));
        }
        System.out.println("Start to check");
        checker.checkAll();
    }
}
