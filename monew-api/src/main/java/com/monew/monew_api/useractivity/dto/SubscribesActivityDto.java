package com.monew.monew_api.useractivity.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.monew.monew_api.useractivity.json.CommaSeparatedToListDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribesActivityDto {

    @JsonProperty("id")
    @JsonAlias({"subscription_id"})
    private String id;

    @JsonProperty("createdAt")
    @JsonAlias({"created_at", "subscription_created_at"})
    private LocalDateTime createdAt;

    @JsonProperty("interestId")
    @JsonAlias({"interest_id"})
    private String interestId;

    @JsonProperty("interestName")
    @JsonAlias({"interest_name"})
    private String interestName;

    @JsonProperty("interestSubscriberCount")
    @JsonAlias({"interest_subscriber_count", "subscriber_count"})
    private Integer interestSubscriberCount;

    @JsonProperty("interestKeywords")
    @JsonAlias({"interest_keywords", "keywords"})
    @JsonDeserialize(using = CommaSeparatedToListDeserializer.class)
    private List<String> interestKeywords;
}