package com.monew.monew_api.useractivity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.useractivity.dto.*;
import com.monew.monew_api.useractivity.repository.projection.UserActivityRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityRawMapper {

    private final ObjectMapper objectMapper;

    /**
     * UserActivityRaw (Record) → UserActivityDto 변환
     */
    public UserActivityDto toDto(UserActivityRaw record) {
        if (record == null) {
            return null;
        }

        return UserActivityDto.builder()
                .id(String.valueOf(record.id()))
                .email(record.email())
                .nickname(record.nickname())
                .createdAt(record.createdAt())
                .subscriptions(parseJsonList(
                        record.subscriptions(),
                        new TypeReference<List<SubscribesActivityDto>>() {}
                ))
                .comments(parseJsonList(
                        record.comments(),
                        new TypeReference<List<CommentActivityDto>>() {}
                ))
                .commentLikes(parseJsonList(
                        record.likes(),
                        new TypeReference<List<CommentLikeActivityDto>>() {}
                ))
                .articleViews(parseJsonList(
                        record.views(),
                        new TypeReference<List<ArticleViewActivityDto>>() {}
                ))
                .build();
    }

    /**
     * JSON String → List<T> 파싱
     */
    private <T> List<T> parseJsonList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return Collections.emptyList();
        }

        try {
            List<T> result = objectMapper.readValue(json, typeRef);
            return result != null ? result : Collections.emptyList();
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패: {}", json, e);
            return Collections.emptyList();
        }
    }
}