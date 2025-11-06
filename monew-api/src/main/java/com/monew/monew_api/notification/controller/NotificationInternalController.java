package com.monew.monew_api.notification.controller;

import com.monew.monew_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationInternalController {

    private final NotificationService notificationService;

    /**
     * 배치 서버가 관심사 관련 기사 등록 알림 생성을 요청하는 엔드포인트
     * 추후 MQ로 전환 예정
     * @param newLinkCountsByInterestId 관심사별 등록된 기사 갯수
     */
    @PostMapping("/api/internal/notifications/articles-registered")
    public void createInterestNotification(@RequestBody Map<Long, Integer> newLinkCountsByInterestId) {
        notificationService.createInterestRegisteredNotification(newLinkCountsByInterestId);
    }
}
