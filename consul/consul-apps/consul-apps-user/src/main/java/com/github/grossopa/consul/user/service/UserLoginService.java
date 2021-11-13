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

package com.github.grossopa.consul.user.service;

import com.github.grossopa.consul.user.exception.UserDoesNotExistException;
import com.github.grossopa.consul.user.model.UserDto;
import com.github.grossopa.consul.user.model.UserLoginDto;
import org.springframework.stereotype.Service;

import static java.util.Arrays.stream;
import static java.util.UUID.randomUUID;

/**
 * login a user
 *
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class UserLoginService {

    private static UserDto[] USERS = new UserDto[]{
            new UserDto("user-00001", "uid1", "John Doe"),
            new UserDto("user-00002", "uid2", "Jack Yin"),
            new UserDto("user-00003", "uid3", "Some Other")
    };

    public UserLoginDto login(String uid, String requestToken) {
        return stream(USERS).filter(user -> user.getUid().endsWith(uid)).map(user -> new UserLoginDto(user,
                requestToken + "___" + randomUUID())).findFirst().orElseThrow(
                () -> new UserDoesNotExistException(uid));
    }
}
