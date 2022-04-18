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
import com.github.grossopa.covid.sh.dao.entity.ShCovidDailyDistrictEntity;
import com.github.grossopa.covid.sh.model.AmapLocationReportRow;
import com.github.grossopa.covid.util.DateUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;

import javax.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

/**
 * Generates the reporting
 *
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class ReportService {

    @Autowired
    ShCrawlerProperties properties;

    @Autowired
    ShCovidDataManager dataManager;

    @Transactional
    @SneakyThrows
    public void generateAmapGeoData() {
        List<ShCovidDailyDistrictEntity> districts = dataManager.findAllDailyDistricts();

        Map<String, AmapLocationReportRow> resultMap = newHashMap();

        districts.forEach(district -> district.getLocations().forEach(location -> {
            String key = district.getDistrict().getName() + location.getName();
            if (resultMap.containsKey(key)) {
                AmapLocationReportRow row = resultMap.get(key);
                row.setCount(row.getCount() + 1);
            } else {
                resultMap.put(key, new AmapLocationReportRow(key, 1,
                        location.getAmapLatitude() + "," + location.getAmapLongitude()));
            }
        }));

        try (CsvBeanWriter mapWriter = new CsvBeanWriter(new FileWriter(
                properties.getReport().getAmapLocation().getFilePath() + "/amap-geo" + DateUtil.formatDateMMdd(
                        new Date()) + ".csv"), STANDARD_PREFERENCE)) {
            String[] header = new String[]{"name", "count", "geoLocation"};
            mapWriter.writeHeader(header);
            resultMap.values().forEach(row -> {
                try {
                    mapWriter.write(row, header);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

        }
    }
}
