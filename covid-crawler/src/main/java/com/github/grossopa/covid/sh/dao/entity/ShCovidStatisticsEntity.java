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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "sh_covid_statistics")
public class ShCovidStatisticsEntity extends AbstractAuditedEntity {

    @Column(name = "type", length = 100, nullable = false)
    private String type;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "display_name", length = 200, nullable = false)
    private String displayName;

    @Column(name = "value", length = 100, nullable = false)
    private String value;

    public static ShCovidStatisticsEntity create(String type, String name, String displayName, String value,
            Date date) {
        ShCovidStatisticsEntity entity = new ShCovidStatisticsEntity();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        entity.setType(type);
        entity.setName(name);
        entity.setDisplayName(displayName);
        entity.setValue(value);
        return entity;
    }
}
