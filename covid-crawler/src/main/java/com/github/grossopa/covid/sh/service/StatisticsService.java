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
import com.github.grossopa.covid.sh.config.properties.StatisticsProperties;
import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyLocationEntity;
import com.github.grossopa.covid.sh.dao.entity.ShCovidStatisticsEntity;
import com.github.grossopa.covid.sh.dao.repository.ShCovidStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Service
@Transactional
public class StatisticsService {

    public static final String TYPE_APPEAR_TIMES = "APPEAR_TIMES";

    @Autowired
    ShCovidStatisticsRepository repository;

    @Autowired
    ShCovidDataManager dataManager;

    @Autowired
    ShCrawlerProperties properties;

    public void updateAll() {
        flatMapLocations();
        updateAppearTimes(20);
    }

    private void flatMapLocations() {
        Date currentTime = new Date();
        StatisticsProperties.FlatMap flatMap = properties.getStatistics().getFlatMap();
        List<String> separators = flatMap.getSeparators();
        List<ShCovidDailyLocationEntity> existingLocations = dataManager.findBySeparators(separators);

        List<ShCovidDailyLocationEntity> newLocations = newArrayList();
        for (ShCovidDailyLocationEntity location : existingLocations) {
            Optional<String> prefixOptional = flatMap.getPrefixes().stream()
                    .filter(prefix -> location.getName().startsWith(prefix)).findFirst();
            if (prefixOptional.isPresent()) {
                log.info("Flatting {} by prefix {}", location.getName(), prefixOptional.get());
                newLocations.addAll(doFlatMap(prefixOptional.get(), location, separators, currentTime));
            } else {
                log.warn("Cannot find prefix for {}", location.getName());
            }
        }

        log.info("Updating {} new locations", newLocations.size());
        dataManager.updateLocations(newLocations);
        log.info("Updating {} existing locations", existingLocations.size());
        dataManager.updateLocations(existingLocations);
    }

    private List<ShCovidDailyLocationEntity> doFlatMap(String prefix, ShCovidDailyLocationEntity location,
            List<String> separators, Date currentTime) {
        String separatorsRegex = String.format("[%s]+", StringUtils.join(separators));
        String[] subLocations = location.getName().replaceFirst(prefix, "").split(separatorsRegex);
        location.setName(prefix + subLocations[0]);
        location.setUpdateTime(currentTime);

        List<ShCovidDailyLocationEntity> newEntities = new ArrayList<>();
        for (int i = 1; i < subLocations.length; i++) {
            ShCovidDailyLocationEntity newLocation = ShCovidDailyLocationEntity.createSimple(prefix + subLocations[i],
                    currentTime, location.getDailyDistrict());
            newLocation.setDailyDistrict(location.getDailyDistrict());
            newEntities.add(newLocation);
        }
        return newEntities;
    }

    private void updateAppearTimes(Integer topN) {
        Map<Long, String> dailyDistrictToNameMap = dataManager.findDailyDistrictMapping();
        List<ShCovidDailyLocationEntity> locationEntities = dataManager.findAllLocations();
        Map<String, List<ShCovidDailyLocationEntity>> map = locationEntities.stream()
                .collect(Collectors.groupingBy(l -> dailyDistrictToNameMap.get(l.getDailyDistrictId()) + l.getName()));

        PriorityQueue<AppearTimes> queue = new PriorityQueue<>(topN, Comparator.comparingInt(a -> a.count));
        map.forEach((key, value) -> {
            queue.add(new AppearTimes(key, value.size()));
            if (queue.size() > topN) {
                queue.remove();
            }
        });

        repository.deleteByType(TYPE_APPEAR_TIMES);
        Date currentTime = new Date();

        List<ShCovidStatisticsEntity> statisticsEntities = queue.stream().map(at -> {
            ShCovidStatisticsEntity entity = new ShCovidStatisticsEntity();
            entity.setUpdateTime(currentTime);
            entity.setCreateTime(currentTime);
            entity.setType(TYPE_APPEAR_TIMES);
            entity.setName(at.getName());
            entity.setDisplayName(at.getName());
            entity.setValue(String.valueOf(at.getCount()));
            return entity;
        }).collect(Collectors.toList());

        repository.saveAll(statisticsEntities);
    }

    @Getter
    @AllArgsConstructor
    public static class AppearTimes {
        private final String name;
        private final Integer count;
    }
}
