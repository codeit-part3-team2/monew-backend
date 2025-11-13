package com.monew.monew_api.useractivity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.useractivity.dto.*;
import com.monew.monew_api.useractivity.repository.projection.UserActivityRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivityRawMapper {

    private final ObjectMapper objectMapper;

    public UserActivityDto toDto(UserActivityRaw record) {
        if (record == null) {
            return null;
        }

        UserActivityDto dto = UserActivityDto.builder()
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

        // HTML 엔티티 디코딩
        decodeHtmlEntities(dto);

        return dto;
    }

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

    /**
     * HTML 엔티티 디코딩 (&quot; → " 등)
     */
    private void decodeHtmlEntities(UserActivityDto dto) {
        // ArticleViews
        if (dto.getArticleViews() != null) {
            dto.getArticleViews().forEach(av -> {
                if (av.getArticleTitle() != null) {
                    av.setArticleTitle(HtmlUtils.htmlUnescape(av.getArticleTitle()));
                }
                if (av.getArticleSummary() != null) {
                    av.setArticleSummary(HtmlUtils.htmlUnescape(av.getArticleSummary()));
                }
            });
        }

        // Comments
        if (dto.getComments() != null) {
            dto.getComments().forEach(c -> {
                if (c.getContent() != null) {
                    c.setContent(HtmlUtils.htmlUnescape(c.getContent()));
                }
                if (c.getArticleTitle() != null) {
                    c.setArticleTitle(HtmlUtils.htmlUnescape(c.getArticleTitle()));
                }
            });
        }

        // CommentLikes
        if (dto.getCommentLikes() != null) {
            dto.getCommentLikes().forEach(cl -> {
                if (cl.getArticleTitle() != null) {
                    cl.setArticleTitle(HtmlUtils.htmlUnescape(cl.getArticleTitle()));
                }
                if (cl.getCommentContent() != null) {
                    cl.setCommentContent(HtmlUtils.htmlUnescape(cl.getCommentContent()));
                }
            });
        }
    }
}