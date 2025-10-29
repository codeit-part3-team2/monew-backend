package com.monew.monew_api.interest.dto.request;

import com.monew.monew_api.interest.dto.InterestOrderBy;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort.Direction;

@ParameterObject
public record CursorPageRequestInterestDto(

    String keyword, // 검색어(관심사, 키워드)
    @NotNull InterestOrderBy orderBy,
    @NotNull Direction direction, // 정렬 방향 (ASC, DESC)
    String cursor, // 커서 값
    LocalDateTime after, //
    @NotNull int limit // 커서 페이지 크기
) {}
