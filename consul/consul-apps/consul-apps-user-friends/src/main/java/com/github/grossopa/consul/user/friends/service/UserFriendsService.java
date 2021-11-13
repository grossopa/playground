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

package com.github.grossopa.consul.user.friends.service;

import com.github.grossopa.consul.user.friends.model.UserFriendsDto;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class UserFriendsService {

    private static Map<String, List<UserFriendsDto>> FRIENDS = ImmutableMap.of("user-00001", newArrayList(
                    new UserFriendsDto("friends-00001", "user-00001", "user-00002",
                            LocalDateTime.of(2021, Month.APRIL, 11, 12, 34), List.of("Best Friend Group")),
                    new UserFriendsDto("friends-00002", "user-00001", "user-00003",
                            LocalDateTime.of(2021, Month.APRIL, 11, 12, 34), List.of("Stranger"))),
            "user-00002", newArrayList(
                    new UserFriendsDto("friends-00003", "user-00002", "user-00001",
                            LocalDateTime.of(2021, Month.APRIL, 11, 12, 34), List.of("Best Friend Group"))),
            "user-00003", newArrayList(
                    new UserFriendsDto("friends-00004", "user-00003", "user-00001",
                            LocalDateTime.of(2021, Month.APRIL, 11, 12, 34), List.of("Default")))
    );

    public List<UserFriendsDto> findAll(String userId) {
        return FRIENDS.getOrDefault(userId, newArrayList());
    }

}
