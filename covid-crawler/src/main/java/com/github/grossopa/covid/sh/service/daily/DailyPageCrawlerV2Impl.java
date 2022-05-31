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
import com.github.grossopa.selenium.core.locator.By2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.strip;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class DailyPageCrawlerV2Impl implements DailyPageCrawler {

    @Autowired
    ShCrawlerProperties properties;

    @Autowired
    ComponentWebDriver driver;

    @Override
    public boolean canCrawl(Date date) {
        return date.after(properties.getDailyV2().getEffectiveDate());
    }

    @Override
    public CovidDaily crawl(Date date, IndexPage page, List<String> districts) {
        Pattern districtConfirmedPattern = Pattern.compile(properties.getDailyV2().getDailyDistrictConfirmedRegex());
        Pattern districtAsymptomaticPattern = Pattern.compile(
                properties.getDailyV2().getDailyDistrictAsymptomaticRegex());

        driver.navigate().to(page.getLink());
        String fullText = driver.findComponent(By2.id("ivs_content")).getText();
        List<String> lines = stream(fullText.split("\n")).map(StringUtils::strip).filter(StringUtils::isNotBlank)
                .collect(toList());

        List<CovidDailyDistrict> result = newArrayList();
        String district = null;
        CovidDailyDistrict dailyDistrict;
        List<String> locations = null;
        for (String text : lines) {
            text = strip(text);
            if (districts.contains(text)) {
                district = findDistrict(districts, text);
                locations = newArrayList();
            } else if (properties.getDailyV2().getDailyDistrictKeywords().stream().allMatch(text::contains)) {
                Integer confirmed = findNumbers(text, districtConfirmedPattern);
                Integer asymptomatic = findNumbers(text, districtAsymptomaticPattern);
                dailyDistrict = new CovidDailyDistrict(district, confirmed, asymptomatic, locations);
                result.add(dailyDistrict);
            } else if (locations != null && !isIgnored(text)) {
                locations.add(text.replaceAll("，$", "").replaceAll("。$", "").replaceAll("、$", "").replaceAll(",$", ""));
            }
        }

        return new CovidDaily(date, page.getLink(), 0, 0,0, 0, 0, result);
    }

    @Override
    public int getOrder() {
        return -20;
    }

    private String findDistrict(List<String> districts, String text) {
        return districts.stream().filter(text::contains).findFirst().orElseThrow();
    }

    private boolean isIgnored(String text) {
        return properties.getDailyV2().getDailyDistrictIgnoreKeywords().stream().anyMatch(text::contains);
    }

    private Integer findNumbers(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            return 0;
        } else {
            return Integer.parseInt(matcher.group(1));
        }
    }
}
