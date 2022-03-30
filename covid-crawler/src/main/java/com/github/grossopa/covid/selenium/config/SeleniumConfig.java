/*
 * Copyright © 2021 the original author or authors.
 *
 * Licensed under the The MIT License (MIT) (the "License");
 *  You may obtain a copy of the License at
 *
 *         https://mit-license.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.grossopa.covid.selenium.config;

import com.github.grossopa.selenium.core.ComponentWebDriver;
import com.github.grossopa.selenium.core.DefaultComponentWebDriver;
import com.github.grossopa.selenium.core.driver.*;
import com.github.grossopa.selenium.core.intercepting.InterceptingWebDriver;
import com.github.grossopa.selenium.core.intercepting.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.remote.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.net.URL;

import static com.github.grossopa.selenium.core.driver.WebDriverType.EDGE;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * The main configuration
 *
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Configuration
@SuppressWarnings("unused")
public class SeleniumConfig {

    @Autowired
    SeleniumProperties seleniumProperties;

    @Bean
    DriverConfig driverConfig() {
        DriverConfig config = new DriverConfig();
        config.setDriverExecutablePath(seleniumProperties.getExecutablePath());
        config.setDriverVersion(seleniumProperties.getDriverVersion());
        config.setPort(seleniumProperties.getPort());
        config.setType(EDGE);
        return config;
    }

    @Bean
    DriverService driverService(DriverConfig config) {
        return config.getType().apply(new CreateDriverServiceAction(), config);
    }

    @Bean
    ComponentWebDriver webDriver(DriverConfig config, DriverService driverService) {
        start(driverService);

        Capabilities options = config.getType().apply(new CreateOptionsAction(), null);
        WebDriver driver = config.getType().apply(new CreateWebDriverFromRunningServiceAction(),
                new RunningServiceParams(options, seleniumProperties.getDriverUrl()));

        Assert.notNull(driver, "driver must not be null");
        return new DefaultComponentWebDriver(new InterceptingWebDriver(driver, new LoggingHandler(100L)));
    }

    @SneakyThrows
    public void start(DriverService driverService) {
        if (isProcessAvailable()) {
            log.info("The process is already started on {}", seleniumProperties.getDriverUrl());
            return;
        }
        log.info("Starting the DriverService on {}", seleniumProperties.getDriverUrl());
        driverService.start();
    }

    @SneakyThrows
    private boolean isProcessAvailable() {
        try {
            URL status = new URL(seleniumProperties.getDriverUrl() + "/status");
            new UrlChecker().waitUntilAvailable(1, SECONDS, status);
            return true;
        } catch (UrlChecker.TimeoutException e) {
            return false;
        }
    }
}
