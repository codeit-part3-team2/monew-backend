package com.monew.monew_api.subscribe.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record SubscribeDto(
    Long id,
    Long interestId,
    String interestName,
    List<String> interestKeywords,
    int interestSubscriberCount,
    LocalDateTime createdAt
) {

}
