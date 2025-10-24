package com.monew.monew_api.useractivity.service.Impl;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.service.UserActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

/*
 * (임시)
 * TODO: 엔티티 작업이 끝난 이후 Repository와 연동하여 실제 데이터를 반환하도록 수정
 */
@Service
public class UserActivityServiceImpl implements UserActivityService {
    @Override
    public UserActivityDto getUserActivity(String userId) {
        return UserActivityDto.builder()
                .id(userId)
                .email("temp@example.com")
                .nickname("임시사용자")
                .createdAt(LocalDateTime.now())
                .subscriptions(new ArrayList<>())
                .comments(new ArrayList<>())
                .commentLikes(new ArrayList<>())
                .articleViews(new ArrayList<>())
                .build();
    }
}
