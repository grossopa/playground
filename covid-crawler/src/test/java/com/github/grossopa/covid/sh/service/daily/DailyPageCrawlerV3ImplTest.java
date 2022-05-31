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
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link DailyPageCrawlerV3Impl}
 *
 * @author Jack Yin
 * @since 1.0
 */
class DailyPageCrawlerV3ImplTest {

    DailyPageCrawlerV3Impl testSubject;
    List<String> districts;

    @BeforeEach
    void setUp() {
        ShCrawlerProperties properties = new ShCrawlerProperties();

        ShCrawlerProperties.DailyV3 dailyV3 = new ShCrawlerProperties.DailyV3();
        dailyV3.setConfirmedRegexes(asList("([0-9]+)例(本土)?(新冠肺炎)?(本土)?确诊病例", "确诊病例([0-9]+)例"));
        dailyV3.setAsymptomaticRegexes(asList("([0-9]+)例本土无症状感染者", "无症状感染者([0-9]+)例"));
        dailyV3.setTitleRegex("[0-9]{2,4}年[0-9]{1,2}月[0-9]{1,2}日，(\\S){2,3}区");
        dailyV3.setIgnoreKeywords(asList("已对相关居住地", "编辑：", "资料：", "滑动查看更多"));

        properties.setDailyV3(dailyV3);

        districts = newArrayList("嘉定区", "奉贤区", "宝山区", "崇明区", "徐汇区", "普陀区", "杨浦区", "松江区", "浦东新区", "虹口区", "金山区", "长宁区",
                "闵行区", "青浦区", "静安区", "黄浦区");

        testSubject = new DailyPageCrawlerV3Impl();
        testSubject.properties = properties;
        testSubject.init();
    }

    @Test
    void testDailyDistrictPattern() {
        assertTrue(testSubject.districtTitlePattern.matcher(
                "2022年4月22日，奉贤区新增6例本土新冠肺炎确诊病例，新增2例本土无症状感染者。上述人员均在隔离管控中发现，其涉及的场所已落实终末消毒等措施。").find());
    }

    @Test
    void parseResult() throws IOException {
        String fullText = IOUtils.toString(
                requireNonNull(DailyPageCrawlerV3ImplTest.class.getResourceAsStream("/daily.txt")), UTF_8);

        CovidDaily daily = testSubject.parseResult(fullText, districts, new Date(), new IndexPage("aa", "bb"));
        List<CovidDailyDistrict> dailyDistricts = daily.getDailyDistricts();
        assertEquals(districts.size(), dailyDistricts.size());
        Map<String, CovidDailyDistrict> districtMap = dailyDistricts.stream()
                .collect(toMap(CovidDailyDistrict::getDistinct, identity()));
        assertDailyDistrict(districtMap.get("浦东新区"), 704, 7257);
        assertDailyDistrict(districtMap.get("黄浦区"), 435, 2408);
        assertDailyDistrict(districtMap.get("静安区"), 76, 710);
        assertDailyDistrict(districtMap.get("徐汇区"), 123, 1152);
        assertDailyDistrict(districtMap.get("长宁区"), 164, 549);
        assertDailyDistrict(districtMap.get("普陀区"), 71, 452);
        assertDailyDistrict(districtMap.get("虹口区"), 428, 569);
        assertDailyDistrict(districtMap.get("杨浦区"), 88, 1268);
        assertDailyDistrict(districtMap.get("宝山区"), 179, 1854);
        assertDailyDistrict(districtMap.get("闵行区"), 208, 1097);
        assertDailyDistrict(districtMap.get("嘉定区"), 76, 424);
        assertDailyDistrict(districtMap.get("金山区"), 0, 26);
        assertDailyDistrict(districtMap.get("松江区"), 120, 2448);
        assertDailyDistrict(districtMap.get("青浦区"), 44, 315);
        assertDailyDistrict(districtMap.get("奉贤区"), 6, 2);
        assertDailyDistrict(districtMap.get("崇明区"), 14, 103);
    }

    private void assertDailyDistrict(CovidDailyDistrict dailyDistrict, int expectedConfirmed, int expectedAsymptomatic) {
        assertNotNull(dailyDistrict);
        assertEquals(expectedConfirmed, dailyDistrict.getConfirmed());
        assertEquals(expectedAsymptomatic, dailyDistrict.getAsymptomatic());
    }
}
