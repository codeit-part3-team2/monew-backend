package com.monew.monew_api.interest.dto.request;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;


public record InterestUpdateRequest(
    @Size(min = 1, max = 10)
    List<String> keywords
) {

}
