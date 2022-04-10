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
import com.github.grossopa.covid.sh.dao.entity.*;
import com.github.grossopa.covid.sh.dao.repository.ShCovidStatisticsRepository;
import com.github.grossopa.covid.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Slf4j
@Service
@Transactional
public class StatisticsService {

    public static final String TYPE_APPEAR_TIMES = "APPEAR_TIMES";
    public static final String TYPE_POPULATION_PERCENTAGE = "TYPE_POPULATION_PERCENTAGE";
    public static final String TYPE_DAILY_NEW_LOCATIONS = "DAILY_NEW_LOCATIONS";

    @Autowired
    ShCovidStatisticsRepository repository;

    @Autowired
    ShCovidDataManager dataManager;

    @Autowired
    ShCrawlerProperties properties;

    public void flatMapLocations() {
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

    public void updateAll() {
        Date currentTime = new Date();
        Map<Long, String> dailyDistrictToNameMap = dataManager.findDailyDistrictMapping();
        List<ShCovidDailyLocationEntity> locationEntities = dataManager.findAllLocations();
        updatePopulationPercentage();
        updateAppearTimes(currentTime, 50, dailyDistrictToNameMap, locationEntities);
        updateDailyNewLocations(currentTime, dailyDistrictToNameMap);
    }

    private void updatePopulationPercentage() {
        repository.deleteByType(TYPE_POPULATION_PERCENTAGE);
        Date currentTime = new Date();
        Map<String, List<ShCovidDailyDistrictEntity>> dailyDistricts = dataManager.findAllDailyDistricts().stream()
                .collect(groupingBy(dd -> dd.getDistrict().getName()));
        List<ShDistrictEntity> districts = dataManager.findDistrictEntities();

        List<ShCovidStatisticsEntity> statisticsEntities = districts.stream().map(district -> {
            int sum = dailyDistricts.get(district.getName()).stream()
                    .mapToInt(d -> d.getAsymptomatic() + d.getConfirmed()).sum();
            return ShCovidStatisticsEntity.create(TYPE_POPULATION_PERCENTAGE, district.getName(), district.getName(),
                    String.valueOf((double) sum / Double.valueOf(district.getPopulation())), currentTime);
        }).collect(toList());

        repository.saveAll(statisticsEntities);
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

    public void updateAppearTimes(Date currentTime, Integer topN, Map<Long, String> dailyDistrictToNameMap,
            List<ShCovidDailyLocationEntity> locationEntities) {
        Map<String, List<ShCovidDailyLocationEntity>> map = locationEntities.stream()
                .collect(groupingBy(toFullName(dailyDistrictToNameMap)));

        PriorityQueue<AppearTimes> queue = new PriorityQueue<>(topN, Comparator.comparingInt(a -> a.count));
        map.forEach((key, value) -> {
            queue.add(new AppearTimes(key, value.size()));
            if (queue.size() > topN) {
                queue.remove();
            }
        });

        repository.deleteByType(TYPE_APPEAR_TIMES);
        List<ShCovidStatisticsEntity> statisticsEntities = queue.stream()
                .map(at -> ShCovidStatisticsEntity.create(TYPE_APPEAR_TIMES, at.getName(), at.getName(),
                        String.valueOf(at.getCount()), currentTime)).collect(toList());

        repository.saveAll(statisticsEntities);
    }

    public void updateDailyNewLocations(Date currentTime, Map<Long, String> dailyDistrictToNameMap) {
        repository.deleteByType(TYPE_DAILY_NEW_LOCATIONS);

        Set<String> existingLocationsMap = newHashSet();
        List<ShCovidStatisticsEntity> statisticsEntities = newArrayList();
        List<ShCovidDailyEntity> dailyList = dataManager.findAllDailyList();
        for (ShCovidDailyEntity daily : dailyList) {
            int[] count = new int[1];
            List<String> newLocationList = newArrayList();
            daily.getDistricts().stream().flatMap(d -> d.getLocations().stream()).forEach(loc -> {
                String location = toFullName(dailyDistrictToNameMap).apply(loc);
                if (existingLocationsMap.add(location)) {
                    newLocationList.add(location);
                    count[0]++;
                }
            });
            String date = DateUtil.formatDate(daily.getDate());

            ShCovidStatisticsEntity statisticsEntity = ShCovidStatisticsEntity.create(TYPE_DAILY_NEW_LOCATIONS, date,
                    date, String.valueOf(count[0]), currentTime);

            statisticsEntities.add(statisticsEntity);
            log.info("New locations of {}: {}", date, StringUtils.join(newLocationList, ","));
        }
        repository.saveAll(statisticsEntities);


    }

    private Function<ShCovidDailyLocationEntity, String> toFullName(Map<Long, String> dailyDistrictToNameMap) {
        return l -> dailyDistrictToNameMap.get(l.getDailyDistrictId()) + l.getName();
    }

    @Getter
    @AllArgsConstructor
    public static class AppearTimes {
        private final String name;
        private final Integer count;
    }
}
