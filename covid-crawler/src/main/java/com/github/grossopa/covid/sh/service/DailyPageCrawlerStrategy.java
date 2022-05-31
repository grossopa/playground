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

package com.github.grossopa.covid.sh.service;

import com.github.grossopa.covid.sh.model.CovidDaily;
import com.github.grossopa.covid.sh.model.IndexPage;
import com.github.grossopa.covid.sh.service.daily.DailyPageCrawler;
import com.github.grossopa.covid.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Component
public class DailyPageCrawlerStrategy {

    @Autowired
    List<DailyPageCrawler> dailyPageCrawlers;

    public CovidDaily crawl(IndexPage page, List<String> districts) {
        Matcher matcher = Pattern.compile("([0-9]+)月([0-9]+)日").matcher(page.getTitle());
        if (!matcher.find()) {
            throw new IllegalStateException("page date not found. " + page);
        }
        int month = parseInt(matcher.group(1));
        int day = parseInt(matcher.group(2));

        Calendar date = DateUtil.createBy(month, day);
        log.info("The page date is : {}", DateUtil.formatDate(date));

        for (DailyPageCrawler dailyPageCrawler : dailyPageCrawlers) {
            if (dailyPageCrawler.canCrawl(date.getTime())) {
                log.info("Using {} to crawl report {}", dailyPageCrawler.getClass(), page);
                return dailyPageCrawler.crawl(date.getTime(), page, districts);
            }
        }

        throw new IllegalStateException("Invalid date");
    }


}
