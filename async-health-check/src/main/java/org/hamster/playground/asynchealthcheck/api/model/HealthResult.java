package org.hamster.playground.asynchealthcheck.api.model;

import java.util.Objects;

/**
 * @author Jack Yin
 * @since 1.0
 */
public class HealthResult {

    private String code;
    private String message;

    public HealthResult() {
    }

    public HealthResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HealthResult)) {
            return false;
        }
        HealthResult that = (HealthResult) o;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "HealthResult{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
    }
}
