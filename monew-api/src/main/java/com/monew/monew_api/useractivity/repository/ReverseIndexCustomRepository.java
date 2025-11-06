package com.monew.monew_api.useractivity.repository;

import java.util.Set;

public interface ReverseIndexCustomRepository {
    /**
     * 특정 인덱스 키에 사용자 ID 추가
     * @param indexKey
     * @param userId
     */
    void addUser(String indexKey, String userId);

    /**
     * 특정 인덱스 키에서 사용자 ID 제거
     * @param indexKey
     * @param userId
     */
    void removeUser(String indexKey, String userId);

    Set<String> findUserIdsByKeys(Set<String> indexKeys);
}
