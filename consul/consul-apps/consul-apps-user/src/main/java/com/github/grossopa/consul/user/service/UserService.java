package com.github.grossopa.consul.user.service;

import com.github.grossopa.consul.user.exception.UserDoesNotExistException;
import com.github.grossopa.consul.user.model.UserDto;
import org.springframework.stereotype.Service;

import static java.util.Arrays.stream;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class UserService {

    private static UserDto[] USERS = new UserDto[]{new UserDto("user-00001", "uid1", "John Doe"),
            new UserDto("user-00002", "uid2", "Jack Yin"), new UserDto("user-00003", "uid3", "Some Other")};

    public UserDto findUser(String userId) {
        return stream(USERS).filter(user -> user.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new UserDoesNotExistException(userId));
    }
}
