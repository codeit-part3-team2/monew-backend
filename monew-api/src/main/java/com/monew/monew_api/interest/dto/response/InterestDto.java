package com.monew.monew_api.interest.dto.response;

import java.util.List;
import lombok.Builder;

public record InterestDto(
    Long id,
    String name,
    List<String> keywords,
    Long subscriberCount,
    boolean subscribedByMe
) {

}
