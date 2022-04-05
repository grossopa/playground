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

package com.github.grossopa.covid.sh.config;

import com.github.grossopa.covid.sh.config.properties.StatisticsProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

/**
 * the configuration
 *
 * @author Jack Yin
 * @since 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wsjkw")
public class ShCrawlerProperties {
    private String indexPageUrl;
    private Integer indexMaxPageSize;
    private List<String> indexTitleKeywords;
    private Long indexSleepMs;
    private List<String> dailyDistrictKeywords;
    private String dailyDistrictConfirmedRegex;
    private String dailyDistrictAsymptomaticRegex;
    private List<String> dailyDistrictIgnoreKeywords;

    private DailyV2 dailyV2;

    private StatisticsProperties statistics;

    @Data
    @Configuration
    @ConfigurationProperties
    public static class DailyV2 {
        private Date effectiveDate;
    }

}
