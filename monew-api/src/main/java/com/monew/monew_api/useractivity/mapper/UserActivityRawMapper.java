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

        UserActivityDto dto = new UserActivityDto();
        dto.setId(String.valueOf(record.id()));
        dto.setEmail(record.email());
        dto.setNickname(record.nickname());
        dto.setCreatedAt(record.createdAt());

        // JSON String → List 변환
        dto.setSubscriptions(parseJsonList(
                record.subscriptions(),
                new TypeReference<List<SubscribesActivityDto>>() {}
        ));
        dto.setComments(parseJsonList(
                record.comments(),
                new TypeReference<List<CommentActivityDto>>() {}
        ));
        dto.setCommentLikes(parseJsonList(
                record.likes(),
                new TypeReference<List<CommentLikeActivityDto>>() {}
        ));
        dto.setArticleViews(parseJsonList(
                record.views(),
                new TypeReference<List<ArticleViewActivityDto>>() {}
        ));

        return dto;
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