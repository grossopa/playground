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

import com.github.grossopa.covid.amap.config.AmapGeoClient;
import com.github.grossopa.covid.amap.model.GeoCode;
import com.github.grossopa.covid.amap.model.GeoCodeResult;
import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyLocationEntity;
import com.github.grossopa.covid.sh.model.CovidDaily;
import com.github.grossopa.covid.sh.model.IndexPage;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.stream.Collectors.toList;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Service
public class ShCovidService {

    @Autowired
    IndexPageCrawler indexPageCrawler;

    @Autowired
    DailyPageCrawlerStrategy dailyPageCrawler;

    @Autowired
    IndexDailyPagePredicate indexDailyPagePredicate;

    @Autowired
    ShCovidDataManager dataManager;

    @Autowired
    ShCovidDailyService dailyService;


    @Autowired
    AmapGeoClient amapGeoClient;

    public void collectData() {
        collectData(indexPageCrawler.crawlIndexPages());
    }

    public void collectData(List<IndexPage> pages) {
        List<String> districts = dataManager.findDistricts();
        List<String> existingUrls = dailyService.findExistingUrls();
        List<IndexPage> filteredPages = pages.stream().filter(indexDailyPagePredicate)
                .filter(p -> !existingUrls.contains(p.getLink())).collect(toList());

        log.info("Found below new daily pages.\n{}", StringUtils.join(filteredPages, "\n"));

        List<CovidDaily> dailyList = filteredPages.stream().map(ip -> dailyPageCrawler.crawl(ip, districts))
                .collect(toList());

        dailyService.save(dailyList);
    }


    @Transactional
    public void refreshLocations() {
        Date currentTime = new Date();
        List<ShCovidDailyLocationEntity> entities = dataManager.findByMissingLocations();
        Map<Long, String> dailyDistrictToNameMap = dataManager.findDailyDistrictMapping();

        List<String> locations = entities.stream()
                .map(e -> dailyDistrictToNameMap.get(e.getDailyDistrictId()) + e.getName()).distinct()
                .collect(toList());
        log.info("Found {} records with {} locations to update.", entities.size(), locations.size());
        Map<String, List<ShCovidDailyLocationEntity>> entityMap = entities.stream()
                .collect(Collectors.groupingBy(e -> dailyDistrictToNameMap.get(e.getDailyDistrictId()) + e.getName()));

        int[] resultCount = new int[]{0, 0};
        List<List<String>> partitions = Lists.partition(locations, 100);
        for (List<String> partition : partitions) {
            Map<String, GeoCodeResult> resultMap = newHashMap();
            for (String location : partition) {
                log.info("Query " + location);
                resultMap.put(location, amapGeoClient.searchGeo(location));
            }
            log.info("Found " + resultMap);
            int[] count = updateLocations(entityMap, resultMap, currentTime);
            resultCount[0] += count[0];
            resultCount[1] += count[1];
            log.info("Partition update result: Success {}, failed {}", count[0], count[1]);
        }
        log.info("Total update result: Success {}, failed {}", resultCount[0], resultCount[1]);
    }

    private int[] updateLocations(Map<String, List<ShCovidDailyLocationEntity>> entityMap,
            Map<String, GeoCodeResult> resultMap, Date currentTime) {
        int successful = 0;
        int failed = 0;
        List<ShCovidDailyLocationEntity> updatedEntities = newArrayList();
        for (Map.Entry<String, GeoCodeResult> resultEntry : resultMap.entrySet()) {
            List<ShCovidDailyLocationEntity> entities = entityMap.get(resultEntry.getKey());
            GeoCodeResult result = resultEntry.getValue();
            if (result.isSuccessful()) {
                successful += entities.size();
                entities.forEach(e -> updateSuccessfulResult(e, result, currentTime));
            } else {
                failed += entities.size();
                entities.forEach(e -> updateFailedResult(e, result, currentTime));
            }
        }
        dataManager.updateLocations(updatedEntities);

        return new int[]{successful, failed};
    }

    private void updateSuccessfulResult(ShCovidDailyLocationEntity entity, GeoCodeResult geoCodeResult,
            Date updateTime) {
        GeoCode code = geoCodeResult.getGeocodes().get(0);
        String[] location = code.getLocation().split(",");

        entity.setAmapAdcode(code.getAdcode());
        entity.setAmapLatitude(new BigDecimal(location[0]));
        entity.setAmapLongitude(new BigDecimal(location[1]));
        entity.setAmapLevel(code.getLevel());
        entity.setAmapStreet(code.getStreet());
        entity.setAmapInfo(geoCodeResult.getInfo());
        entity.setAmapStatus(geoCodeResult.getStatus());
        entity.setUpdateTime(updateTime);
    }

    private void updateFailedResult(ShCovidDailyLocationEntity entity, GeoCodeResult geoCodeResult, Date updateTime) {
        entity.setAmapInfo(geoCodeResult.getInfo());
        entity.setAmapStatus(geoCodeResult.getStatus());
        entity.setUpdateTime(updateTime);
    }

}
