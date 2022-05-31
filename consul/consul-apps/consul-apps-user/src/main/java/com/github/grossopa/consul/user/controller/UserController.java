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

package com.github.grossopa.consul.user.controller;

import com.github.grossopa.consul.user.model.UserLoginDto;
import com.github.grossopa.consul.user.model.UserLoginRequestDto;
import com.github.grossopa.consul.user.model.UserProfileResponseDto;
import com.github.grossopa.consul.user.service.UserLoginService;
import com.github.grossopa.consul.user.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Api(tags = "user")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    UserProfileService userProfileService;

    @ApiOperation("login")
    @PostMapping("/login")
    public UserLoginDto login(@RequestBody UserLoginRequestDto requestDto) {
        log.info("User login request {}", requestDto.getUid());
        log.debug("Request token {}", requestDto.getRequestToken());

        return userLoginService.login(requestDto.getUid(), requestDto.getRequestToken());
    }

    @ApiOperation("profile")
    @GetMapping("/{userId}/profile")
    public UserProfileResponseDto profile(@PathVariable("userId") String userId) {
        log.info("User profile request {}", userId);

        return userProfileService.findUserProfile(userId, "___");
    }
}
