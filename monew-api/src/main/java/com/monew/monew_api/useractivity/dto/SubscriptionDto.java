package com.monew.monew_api.useractivity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * (임시)
 * TODO: 팀원 작업 완료 후 com.monew.monew_api.interest.dto.SubscriptionDto로 교체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    private String id;
    private String interestId;
    private String interestName;
    private List<String> interestKeywords;
    private Long interestSubscriberCount;
    private LocalDateTime createdAt;
}