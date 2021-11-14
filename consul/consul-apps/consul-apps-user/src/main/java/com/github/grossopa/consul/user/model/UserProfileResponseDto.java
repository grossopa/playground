package com.github.grossopa.consul.user.model;

import com.github.grossopa.consul.user.model.friends.UserFriendsDto;
import com.github.grossopa.consul.user.model.video.UserVideoHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class UserProfileResponseDto {
    @Nonnull
    private UserDto user;
    @Nonnull
    private List<UserFriendsDto> friends;
    @Nonnull
    private List<UserVideoHistoryDto> videoHistories;

    public static UserProfileResponseDto of(UserDto user, List<UserFriendsDto> friends,
            List<UserVideoHistoryDto> videoHistories) {
        return new UserProfileResponseDto(user, friends == null ? List.of() : friends,
                videoHistories == null ? List.of() : videoHistories);
    }

}
