package com.monew.monew_api.useractivity.controller;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;

    /*
     single query 방식으로 사용자 활동내역 조회
     */
    @GetMapping("/{userId}/temp")
    public ResponseEntity<UserActivityDto> getUserActivity2(@PathVariable String userId) {
        log.info("활동내역 조회 요청: userId={}", userId);

        UserActivityDto activity = userActivityService.getUserActivitySingleQuery(userId);

        return ResponseEntity.ok(activity);
    }

    /*
     여러 query 방식으로 사용자 활동내역 조회
     사용시 엔드포인트와 메서드명 변경 필요
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable String userId) {
        log.info("활동내역 조회 요청: userId={}", userId);

        UserActivityDto activity = userActivityService.getUserActivity(userId);

        return ResponseEntity.ok(activity);
    }
}