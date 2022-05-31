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

package com.github.grossopa.covid.sh.service.daily;

import com.github.grossopa.covid.sh.config.ShCrawlerProperties;
import com.github.grossopa.covid.sh.model.CovidDaily;
import com.github.grossopa.covid.sh.model.CovidDailyDistrict;
import com.github.grossopa.covid.sh.model.IndexPage;
import com.github.grossopa.selenium.core.ComponentWebDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.strip;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class DailyPageCrawlerV3Impl implements DailyPageCrawler {

    @Autowired
    ShCrawlerProperties properties;

    @Autowired
    ComponentWebDriver driver;

    List<Pattern> summaryPatterns;

    Pattern districtTitlePattern;

    List<Pattern> districtConfirmedPatterns;
    List<Pattern> districtAsymptomaticPatterns;


    @PostConstruct
    public void init() {
        ShCrawlerProperties.DailyV3 dailyV3 = properties.getDailyV3();
        summaryPatterns = dailyV3.getSummaryRegexes().stream().map(Pattern::compile).collect(toUnmodifiableList());
        districtTitlePattern = Pattern.compile(dailyV3.getTitleRegex());
        districtConfirmedPatterns = dailyV3.getConfirmedRegexes().stream().map(Pattern::compile)
                .collect(toUnmodifiableList());
        districtAsymptomaticPatterns = dailyV3.getAsymptomaticRegexes().stream().map(Pattern::compile)
                .collect(toUnmodifiableList());
    }

    @Override
    public boolean canCrawl(Date date) {
        return date.after(properties.getDailyV3().getEffectiveDate());
    }

    @Override
    public CovidDaily crawl(Date date, IndexPage page, List<String> districts) {
        driver.navigate().to(page.getLink());
        driver.threadSleep(3000);

        String fullText = (String) driver.executeScript("return document.getElementById('js_content').innerText;");
        return parseResult(fullText, districts, date, page);
    }

    CovidDaily parseResult(String fullText, List<String> districts, Date date, IndexPage page) {
        List<String> lines = stream(fullText.split("\n")).map(StringUtils::strip).filter(StringUtils::isNotBlank)
                .collect(toList());
        int confirmed = 0;
        int asymptomatic = 0;
        int closedLoopConverted = 0;
        int closedLoopConfirmed = 0;
        int closedLoopAsymptomatic = 0;
        while (!lines.isEmpty() && confirmed == 0 && asymptomatic == 0 && closedLoopConfirmed == 0 && closedLoopAsymptomatic == 0) {
            String line = lines.remove(0);
            for (Pattern pattern : this.summaryPatterns) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    confirmed = Integer.parseInt(matcher.group(1));
                    asymptomatic = Integer.parseInt(matcher.group(2));
                    closedLoopConverted = Integer.parseInt(matcher.group(3));
                    closedLoopConfirmed = Integer.parseInt(matcher.group(4));
                    closedLoopAsymptomatic = Integer.parseInt(matcher.group(5));
                    break;
                }
            }
        }

        List<CovidDailyDistrict> result = parseDailyDistricts(lines, districts);
        return new CovidDaily(date, page.getLink(), confirmed, asymptomatic, closedLoopConverted, closedLoopConfirmed,
                closedLoopAsymptomatic, result);
    }


    List<CovidDailyDistrict> parseDailyDistricts(List<String> lines, List<String> districts) {
        List<CovidDailyDistrict> result = newArrayList();
        List<String> locations = null;
        for (String text : lines) {
            text = strip(text);
            if (districts.contains(text)) {
                // skip the district information
                continue;
            }

            Matcher matcher = districtTitlePattern.matcher(text);
            if (matcher.find()) {
                String district = districts.stream().filter(text::contains).findFirst().orElseThrow();
                Integer confirmed = findNumbers(text, districtConfirmedPatterns);
                Integer asymptomatic = findNumbers(text, districtAsymptomaticPatterns);
                locations = newArrayList();
                result.add(new CovidDailyDistrict(district, confirmed, asymptomatic, locations));
            } else if (locations != null && !isIgnored(text)) {
                locations.add(
                        text.replaceAll(" ", "").replaceAll("\\s+$", "").replaceAll("\\t+$", "").replaceAll("，$", "")
                                .replaceAll("。$", "").replaceAll("、$", "").replaceAll(",$", ""));
            }
        }

        return result;
    }

    @Override
    public int getOrder() {
        return -30;
    }

    private String findDistrict(List<String> districts, String text) {
        return districts.stream().filter(text::contains).findFirst().orElseThrow();
    }

    private boolean isIgnored(String text) {
        return properties.getDailyV3().getIgnoreKeywords().stream().anyMatch(text::contains);
    }

    private Integer findNumbers(String text, List<Pattern> patterns) {
        int result = 0;
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                result = Integer.parseInt(matcher.group(1));
            }
        }
        return result;
    }
}
