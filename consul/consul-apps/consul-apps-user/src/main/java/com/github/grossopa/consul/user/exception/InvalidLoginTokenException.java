package com.github.grossopa.consul.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Nullable;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Getter
public class InvalidLoginTokenException extends HttpClientErrorException {

    private final String userId;
    @Nullable
    private final String loginToken;

    public InvalidLoginTokenException(String userId, @Nullable String loginToken) {
        super(HttpStatus.UNAUTHORIZED);
        this.userId = userId;
        this.loginToken = loginToken;
    }
}
