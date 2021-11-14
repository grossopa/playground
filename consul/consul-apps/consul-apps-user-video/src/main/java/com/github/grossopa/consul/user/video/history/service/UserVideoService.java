package com.github.grossopa.consul.user.video.history.service;

import com.github.grossopa.consul.user.video.history.model.UserVideoHistoryDto;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Jack Yin
 * @since 1.0
 */
@Service
public class UserVideoService {

    private Map<String, List<UserVideoHistoryDto>> userVideoMap;

    @PostConstruct
    public void postConstruct() {
        userVideoMap = newHashMap();
        mockAndInsertVideoHistory("user-00001", "https://some-url/some-video-1",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 2));
        mockAndInsertVideoHistory("user-00001", "https://some-url/some-video-2",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 3));
        mockAndInsertVideoHistory("user-00001", "https://some-url/some-video-3",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 4));
        mockAndInsertVideoHistory("user-00001", "https://some-url/some-video-4",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 5));
        mockAndInsertVideoHistory("user-00001", "https://some-url/some-video-5",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 6));
        mockAndInsertVideoHistory("user-00002", "https://some-url/some-video-6",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 7));
        mockAndInsertVideoHistory("user-00002", "https://some-url/some-video-7",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 8));
        mockAndInsertVideoHistory("user-00002", "https://some-url/some-video-8",
                LocalDateTime.of(2021, Month.JANUARY, 3, 1, 9));

    }

    private void mockAndInsertVideoHistory(String userId, String url, LocalDateTime time) {
        UserVideoHistoryDto userVideoDto = new UserVideoHistoryDto(UUID.randomUUID().toString(), userId, url, time);
        if (!userVideoMap.containsKey(userId)) {
            userVideoMap.put(userId, newArrayList());
        }

        userVideoMap.get(userId).add(userVideoDto);
    }

    public List<UserVideoHistoryDto> findUserVideoHistory(String userId) {
        return userVideoMap.get(userId);
    }
}
