package com.github.grossopa.consul.user.model.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoHistoryDto {
    private String id;
    private String userId;
    private String videoUrl;
    private LocalDateTime viewedOn;
}
