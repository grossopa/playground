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

import com.github.grossopa.covid.sh.config.ShCrawlerProperties;
import com.github.grossopa.covid.sh.model.IndexPage;
import com.github.grossopa.selenium.core.ComponentWebDriver;
import com.github.grossopa.selenium.core.component.WebComponent;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static com.github.grossopa.selenium.core.locator.By2.xpathBuilder;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.random;
import static java.util.stream.Collectors.toList;

/**
 * Crawls each page
 *
 * @author Jack Yin
 * @since 1.0
 */
@Component
public class IndexPageCrawler {

    @Autowired
    ShCrawlerProperties properties;

    @Autowired
    ComponentWebDriver driver;

    @SuppressWarnings("BusyWait")
    @SneakyThrows
    public List<IndexPage> crawlIndexPages() {
        List<IndexPage> result = newArrayList();
        for (int i = 1; i <= properties.getIndexMaxPageSize(); i++) {
            result.addAll(crawlIndexPage(i));
            Thread.sleep(properties.getIndexSleepMs() + (long) (random() * 1000L));
        }
        return result;
    }

    private List<IndexPage> crawlIndexPage(int pageIndex) {
        driver.navigate().to(getPageUrl(pageIndex));

        WebComponent newsContainer = driver.findComponent(
                xpathBuilder().anywhere().attr("class").exact("container").descendant("ul").attr("class")
                        .contains("list-date").build());

        List<WebComponent> newsEntries = newsContainer.findComponents(By.tagName("li"));

        return newsEntries.stream().map(c -> c.findComponent(By.tagName("a")))
                .map(child -> new IndexPage(child.getText(), child.getAttribute("href"))).collect(toList());
    }

    private String getPageUrl(int pageIndex) {
        return MessageFormat.format(properties.getIndexPageUrl(), pageIndex == 1 ? "" : "_" + pageIndex);
    }
}
