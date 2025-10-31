package com.monew.monew_api.interest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseInterestDto(
    List<InterestDto> content, // 실제 데이터 목록
    String nextCursor, // 다음 페이지 요청 위한 커서
    LocalDateTime nextAfter, // 커서 기준 시점
    int size, // 한페이지에 담긴 데이터 개수
    Long totalElements, // 전체 데이터 개수
    boolean hasNext // 다음 페이지 존재 여부

) {

}
