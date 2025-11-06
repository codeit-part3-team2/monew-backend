package com.monew.monew_api.useractivity.service;

import java.util.Set;

public interface ReverseIndexService {
    /**
     * 특정 인덱스 키에 사용자 ID 추가
     * @param indexKey cacheDB key 값
     * @param userId   사용자 ID
     */
    void addUser(String indexKey, String userId);

    /**
     * 특정 인덱스 키에서 사용자 ID 제거
     * @param indexKey cacheDB key 값
     * @param userId   사용자 ID
     */
    void removeUser(String indexKey, String userId);

    /**
     * 특정 인덱스 키에 해당하는 모든 사용자 ID 조회
     * @param indexKey cacheDB key 값
     * @return 사용자 ID 집합
     */
    Set<String> getUserIds(String indexKey);

    /**
     * 여러 인덱스 키에 해당하는 모든 사용자 ID 조회
     * @param indexKeys cacheDB key 값 집합
     * @return 사용자 ID 집합
     */
    Set<String> getUserIds(Set<String> indexKeys);

    /**
     * 여러 인덱스 키 삭제
     * @param indexKeys cacheDB key 값 집합
     */
    void deleteIndexes(Set<String> indexKeys);
}
