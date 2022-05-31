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

import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyDistrictEntity;
import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyEntity;
import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyLocationEntity;
import com.github.grossopa.covid.sh.dao.entity.ShDistrictEntity;
import com.github.grossopa.covid.sh.dao.repository.ShCovidDailyDistrictRepository;
import com.github.grossopa.covid.sh.dao.repository.ShCovidDailyRepository;
import com.github.grossopa.covid.sh.dao.repository.ShCovidLocationRepository;
import com.github.grossopa.covid.sh.dao.repository.ShDistinctRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static javax.transaction.Transactional.TxType.NOT_SUPPORTED;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
@Transactional
public class ShCovidDataManager {

    @Autowired
    ShDistinctRepository distinctRepository;

    @Autowired
    ShCovidDailyDistrictRepository dailyDistrictRepository;

    @Autowired
    ShCovidLocationRepository locationRepository;

    @Autowired
    ShCovidDailyRepository dailyRepository;

    public List<String> findDistricts() {
        return distinctRepository.findAll(Sort.by("id").ascending()).stream().map(ShDistrictEntity::getName)
                .collect(toList());
    }

    public List<ShDistrictEntity> findDistrictEntities() {
        return newArrayList(distinctRepository.findAll(Sort.by("id").ascending()));
    }

    public List<ShCovidDailyLocationEntity> findByMissingLocations() {
        return locationRepository.findByAmapLatitude(null);
    }

    public List<ShCovidDailyLocationEntity> findAllLocations() {
        return newArrayList(locationRepository.findAll());
    }

    public List<ShCovidDailyLocationEntity> findBySeparators(List<String> separators) {
        return separators.stream().flatMap(s -> locationRepository.findByNameLike("%" + s + "%").stream())
                .collect(toList());
    }

    /**
     * Returns dailyDistrict to distinct name mapping
     *
     * @return dailyDistrict to distinct name mapping
     */
    public Map<Long, String> findDailyDistrictMapping() {
        Map<Long, ShDistrictEntity> districtMap = newArrayList(distinctRepository.findAll()).stream()
                .collect(toMap(ShDistrictEntity::getId, identity()));
        Iterable<ShCovidDailyDistrictEntity> dailyDistrictEntities = dailyDistrictRepository.findAll();
        Map<Long, String> result = newHashMap();
        for (ShCovidDailyDistrictEntity dailyDistrictEntity : dailyDistrictEntities) {
            result.put(dailyDistrictEntity.getId(), districtMap.get(dailyDistrictEntity.getDistrictId()).getName());
        }
        return result;
    }

    @Transactional(NOT_SUPPORTED)
    public void updateLocations(List<ShCovidDailyLocationEntity> locationEntities) {
        this.locationRepository.saveAll(locationEntities);
    }

    public List<ShCovidDailyDistrictEntity> findAllDailyDistricts() {
        return newArrayList(this.dailyDistrictRepository.findAll());
    }

    public List<ShCovidDailyEntity> findAllDailyList() {
        return newArrayList(this.dailyRepository.findAll(Sort.by(Sort.Order.asc("date"))));
    }
}
