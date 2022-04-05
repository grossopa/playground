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

package com.github.grossopa.covid;

import com.github.grossopa.covid.sh.service.ShCovidService;
import com.github.grossopa.covid.sh.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * To crawl the information and stores in CSV
 *
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class CovidCrawlerApplication {


    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CovidCrawlerApplication.class);
        SpringApplication application = builder.build();
        ConfigurableApplicationContext context = application.run(
                ArrayUtils.add(args, "--spring.config.location=classpath:/application.yaml,file:./secrets/amap.yaml"));

        context.getBean(ShCovidService.class).collectData();
        context.getBean(ShCovidService.class).refreshLocations();
        context.getBean(StatisticsService.class).updateAll();
    }
}
