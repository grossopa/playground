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

package com.github.grossopa.covid.amap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * The Geo code of Geo search response.
 *
 * @author Jack Yin
 * @since 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class GeoCode {

    private String location;

    @JsonIgnore
    private String adcode;
    @JsonIgnore
    private String street;
    @JsonIgnore
    private String level;

    /**
     * Sets the street value as object, the JSON result will be either string value or empty array [].
     *
     * @param street the street value to set, either string or empty array.
     */
    @JsonSetter("street")
    public void setStreet(Object street) {
        this.street = street instanceof String ? String.valueOf(street) : "";
    }

    /**
     * Sets the level value as object, the JSON result will be either string value or empty array [].
     *
     * @param level the level value to set, either string or empty array.
     */
    @JsonSetter("level")
    public void setLevel(Object level) {
        this.level = level instanceof String ? String.valueOf(level) : "";
    }

    /**
     * Sets the adcode value as object, the JSON result will be either string value or empty array [].
     *
     * @param adcode the level value to set, either string or empty array.
     */
    @JsonSetter("adcode")
    public void setAdcode(Object adcode) {
        this.adcode = adcode instanceof String ? String.valueOf(adcode) : "";
    }

}
