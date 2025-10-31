package com.monew.monew_api.interest.mapper;

import com.monew.monew_api.interest.dto.response.InterestDto;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.InterestKeyword;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface InterestMapper {

  InterestDto toInterestDto(Interest interest, List<String> keywords, Boolean subscribedByMe);

  @Mapping(target = "subscriberCount", source = "subscriberCount")
  InterestDto toInterestDto(Interest interest, List<String> keywords, Boolean subscribedByMe,
      int subscriberCount);

  // 커스텀 매핑 메서드 (Set<InterestKeyword> -> List<String>)
  default List<String> map(Set<InterestKeyword> interestKeywords) {
    if (interestKeywords == null) {
      return Collections.emptyList();
    }
    return interestKeywords.stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .toList();
  }
}
