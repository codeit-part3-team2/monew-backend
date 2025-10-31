package com.monew.monew_api.subscribe.mapper;

import com.monew.monew_api.interest.entity.InterestKeyword;
import com.monew.monew_api.subscribe.dto.SubscribeDto;
import com.monew.monew_api.subscribe.entity.Subscribe;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SubscribeMapper {

  @Mappings({
      @Mapping(source = "interest.id", target = "interestId"),
      @Mapping(source = "interest.name", target = "interestName"),
      @Mapping(source = "interest.keywords", target = "interestKeywords"),
      @Mapping(source = "interest.subscriberCount", target = "interestSubscriberCount")
  })
  SubscribeDto toSubscribeDto(Subscribe subscribe);

  default List<String> map(Set<InterestKeyword> keywords) {
    if (keywords == null) return null;
    return keywords.stream()
        .map(ik -> ik.getKeyword().getKeyword())
        .collect(Collectors.toList());
  }
}
