package com.monew.monew_api.interest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestRegisterRequest(

    @JsonProperty("name")
    @Size(min = 1, max = 50)
    String name,

    @Size(min = 1, max = 10)
    List<String> keywords
) {

}
