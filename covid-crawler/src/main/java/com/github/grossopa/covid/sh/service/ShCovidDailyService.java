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
import com.github.grossopa.covid.sh.dao.repository.ShCovidDailyRepository;
import com.github.grossopa.covid.sh.dao.repository.ShDistinctRepository;
import com.github.grossopa.covid.sh.model.CovidDaily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
@Transactional
public class ShCovidDailyService {

    @Autowired
    ShDistinctRepository shDistrictRepository;
    @Autowired
    ShCovidDailyRepository repository;

    public List<String> findExistingUrls() {
        return newArrayList(repository.findAll()).stream().map(ShCovidDailyEntity::getUrl).collect(toList());
    }

    public void save(List<CovidDaily> dailyList) {
        Date currentTime = new Date();
        Map<String, ShDistrictEntity> districtEntityMap = newArrayList(shDistrictRepository.findAll()).stream()
                .collect(toMap(ShDistrictEntity::getName, identity()));

        List<ShCovidDailyEntity> dailyEntities = dailyList.stream().map(d -> {
            ShCovidDailyEntity dailyEntity = new ShCovidDailyEntity();
            dailyEntity.setDate(d.getDate());
            dailyEntity.setUrl(d.getUrl());
            dailyEntity.setCreateTime(currentTime);
            dailyEntity.setUpdateTime(currentTime);
            dailyEntity.setConfirmed(d.getConfirmed());
            dailyEntity.setAsymptomatic(d.getAsymptomatic());
            dailyEntity.setClosedLoopConverted(d.getClosedLoopConverted());
            dailyEntity.setClosedLoopConfirmed(d.getClosedLoopConfirmed());
            dailyEntity.setClosedLoopAsymptomatic(d.getClosedLoopAsymptomatic());

            List<ShCovidDailyDistrictEntity> districts = d.getDailyDistricts().stream().map(dist -> {
                ShCovidDailyDistrictEntity dailyDistrictEntity = new ShCovidDailyDistrictEntity();
                dailyDistrictEntity.setDistrict(districtEntityMap.get(dist.getDistinct()));
                dailyDistrictEntity.setConfirmed(dist.getConfirmed());
                dailyDistrictEntity.setAsymptomatic(dist.getAsymptomatic());
                dailyDistrictEntity.setCreateTime(currentTime);
                dailyDistrictEntity.setUpdateTime(currentTime);

                List<ShCovidDailyLocationEntity> locations = dist.getLocations().stream()
                        .map(loc -> ShCovidDailyLocationEntity.createSimple(loc, currentTime, dailyDistrictEntity))
                        .collect(toList());
                dailyDistrictEntity.setLocations(locations);
                return dailyDistrictEntity;
            }).collect(toList());
            dailyEntity.setDistricts(districts);
            return dailyEntity;
        }).collect(toList());

        repository.saveAll(dailyEntities);
    }

}
