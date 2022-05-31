package com.github.grossopa.consul.user.service;

import org.springframework.stereotype.Service;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class LoginTokenValidationService {

    public boolean validate(String loginToken) {
        return loginToken != null && loginToken.contains("___");
    }
}
