package com.monew.monew_api.common.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(max = 100, message = "닉네임은 100자를 초과할 수 없습니다.")
    private String nickname;

    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }

    public boolean hasNickname() {
        return nickname != null && !nickname.isBlank();
    }
}
