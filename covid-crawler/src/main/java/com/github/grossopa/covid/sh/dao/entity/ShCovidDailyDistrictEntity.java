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
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "sh_covid_daily_district")
public class ShCovidDailyDistrictEntity extends AbstractAuditedEntity {
    @ManyToOne
    @JoinColumn(name = "district_id")
    private ShDistrictEntity district;

    @Column(name = "confirmed", nullable = false)
    private Integer confirmed;

    @Column(name = "asymptomatic", nullable = false)
    private Integer asymptomatic;

    @Column(name = "pre_ten_thousands")
    private Integer perTenThousands;

    @Column(name = "new_location")
    private Integer newLocation;

    @OneToMany(cascade = ALL, mappedBy = "dailyDistrict")
    private List<ShCovidDailyLocationEntity> locations;

    @Column(name = "district_id", updatable = false, insertable = false)
    private Long districtId;


}
