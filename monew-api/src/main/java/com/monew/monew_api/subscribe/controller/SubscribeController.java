package com.monew.monew_api.subscribe.controller;

import com.monew.monew_api.subscribe.dto.SubscribeDto;
import com.monew.monew_api.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Slf4j
public class SubscribeController {

  private final SubscribeService subscribeService;

  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<SubscribeDto> createSubscribe(
      @PathVariable Long interestId,
      @RequestHeader("Monew-Request-User-ID") Long userId){
    log.info("[API 요청] POST/api/interests/{}/subscriptions - 관심사 구독 요청", interestId);
    SubscribeDto subscribeDto = subscribeService.createSubscribe(interestId, userId);
    log.info("[API 요청] POST/api/interests/{}/subscriptions - 관심사 구독 응답", interestId);
    return ResponseEntity.status(HttpStatus.CREATED).body(subscribeDto);
  }

  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<Void> deleteSubscribe(
      @PathVariable Long interestId,
      @RequestHeader("Monew-Request-User-ID") Long userId){
    log.info("[API 요청] DELETE/api/interests/{}/subscriptions - 구독 취소 요청", interestId);
    subscribeService.deleteSubscribe(interestId, userId);
    log.info("[API 요청] DELETE/api/interests/{}/subscriptions - 구독 취소 응답", interestId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
