package com.monew.monew_api.interest.dto.response;

import java.util.List;

public record InterestDto(
    Long id,
    String name,
    List<String> keywords,
    int subscriberCount,
    boolean subscribedByMe
) {

}
