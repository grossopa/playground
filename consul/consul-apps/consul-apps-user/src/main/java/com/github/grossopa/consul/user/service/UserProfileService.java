package com.github.grossopa.consul.user.service;

import com.github.grossopa.consul.user.exception.InvalidLoginTokenException;
import com.github.grossopa.consul.user.model.UserDto;
import com.github.grossopa.consul.user.model.UserProfileResponseDto;
import com.github.grossopa.consul.user.model.friends.UserFriendsDto;
import com.github.grossopa.consul.user.model.video.UserVideoHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class UserProfileService {

    @Autowired
    UserService userService;

    @Autowired
    LoginTokenValidationService loginTokenValidationService;

    @Resource(name = "userFriendsRestTemplate")
    RestTemplate userFriendsRestTemplate;

    @Resource(name = "userVideoRestTemplate")
    RestTemplate userVideoRestTemplate;

    public UserProfileResponseDto findUserProfile(String userId, String loginToken) {
        if (!loginTokenValidationService.validate(loginToken)) {
            throw new InvalidLoginTokenException(userId, loginToken);
        }

        List<UserFriendsDto> friends = userFriendsRestTemplate.exchange(String.format("/%s/all-friends", userId),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<UserFriendsDto>>() {
                    // implementation is not necessary
                }).getBody();

        List<UserVideoHistoryDto> videoHistories = userVideoRestTemplate.exchange(
                String.format("/%s/all-video-history", userId), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserVideoHistoryDto>>() {
                    // implementation is not necessary
                }).getBody();

        UserDto user = userService.findUser(userId);

        return UserProfileResponseDto.of(user, friends, videoHistories);
    }
}
