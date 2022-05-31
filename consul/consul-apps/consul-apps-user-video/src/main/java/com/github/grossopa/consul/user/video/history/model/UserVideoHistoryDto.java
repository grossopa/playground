package com.github.grossopa.consul.user.video.history.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class UserVideoHistoryDto {
    private String id;
    private String userId;
    private String videoUrl;
    private LocalDateTime viewedOn;
}
