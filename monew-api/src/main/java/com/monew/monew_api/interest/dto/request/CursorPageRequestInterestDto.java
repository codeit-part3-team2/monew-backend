package com.monew.monew_api.interest.dto.request;

import com.monew.monew_api.interest.dto.InterestOrderBy;
import com.querydsl.core.types.Order;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record CursorPageRequestInterestDto(

    String keyword, // 검색어(관심사, 키워드)
    @NotNull InterestOrderBy orderBy,
    @NotNull Order direction, // 정렬 방향 (ASC, DESC)
    String cursor, // 커서 값
    LocalDateTime after, //
    @NotNull Integer limit // 커서 페이지 크기
){
}
