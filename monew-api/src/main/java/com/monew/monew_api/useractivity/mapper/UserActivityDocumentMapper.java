package com.monew.monew_api.useractivity.mapper;

import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.UserActivityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserActivityDocumentMapper {
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    UserActivityCacheDocument toDocument(UserActivityDto dto);
}
