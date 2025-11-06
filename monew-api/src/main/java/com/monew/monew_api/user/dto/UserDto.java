package com.monew.monew_api.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
}
