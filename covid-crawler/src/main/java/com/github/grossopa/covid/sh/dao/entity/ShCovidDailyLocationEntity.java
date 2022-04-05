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

package com.github.grossopa.covid.sh.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "sh_covid_daily_location", indexes = {@Index(name = "idx_name", columnList = "name")})
public class ShCovidDailyLocationEntity extends AbstractAuditedEntity {

    @Column(name = "name", length = 1000, nullable = false)
    private String name;

    @Column(name = "amap_adcode", length = 50)
    private String amapAdcode;

    @Column(name = "amap_level", length = 50)
    private String amapLevel;

    @Column(name = "amap_street", length = 1000)
    private String amapStreet;

    @Column(name = "amap_latitude")
    private BigDecimal amapLatitude;

    @Column(name = "amap_longitude")
    private BigDecimal amapLongitude;

    @Column(name = "amap_status", length = 50)
    private String amapStatus;

    @Column(name = "amap_info", length = 50)
    private String amapInfo;

    @Column(name = "sh_covid_daily_district_id", updatable = false, insertable = false)
    private Long dailyDistrictId;

    @ManyToOne
    @JoinColumn(name = "sh_covid_daily_district_id")
    private ShCovidDailyDistrictEntity dailyDistrict;

    public static ShCovidDailyLocationEntity createSimple(String name, Date createTime,
            ShCovidDailyDistrictEntity dailyDistrict) {
        ShCovidDailyLocationEntity result = new ShCovidDailyLocationEntity();
        result.setName(name);
        result.initTime(createTime);
        result.setDailyDistrict(dailyDistrict);
        return result;
    }
}
