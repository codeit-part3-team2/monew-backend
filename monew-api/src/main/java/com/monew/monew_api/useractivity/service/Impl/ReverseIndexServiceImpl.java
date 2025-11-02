package com.monew.monew_api.useractivity.service.Impl;

import com.monew.monew_api.useractivity.document.ReverseIndexDocument;
import com.monew.monew_api.useractivity.repository.ReverseIndexRepository;
import com.monew.monew_api.useractivity.service.ReverseIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReverseIndexServiceImpl implements ReverseIndexService {

    private final ReverseIndexRepository reverseIndexRepository;

    /**
     * 역인덱스에 사용자 추가
     * document key 형태 그대로
     * indexKey (예: "comment_123_likes")
     */
    @Override
    @Transactional
    public void addUser(String indexKey, String userId) {
        reverseIndexRepository.addUser(indexKey, userId);
        log.debug("[ReverseIndex] add: key={}, user={}", indexKey, userId);
    }

    /**
     * 역인덱스에서 사용자 제거
     */
    public void removeUser(String indexKey, String userId) {
        reverseIndexRepository.removeUser(indexKey, userId);
        log.debug("[ReverseIndex] remove: key={}, user={}", indexKey, userId);
    }

    /**
     * 역인덱스에서 영향받는 사용자 ID 조회
     */
    @Override
    public Set<String> getUserIds(String indexKey) {
        return reverseIndexRepository.findById(indexKey)
                .map(ReverseIndexDocument::getUserIds)
                .orElse(Collections.emptySet());
    }

    /**
     * 여러 인덱스 키에서 사용자 ID 조회
     */
    @Override
    public Set<String> getUserIds(Set<String> indexKeys) {
        return reverseIndexRepository.findUserIdsByKeys(indexKeys);
    }

    /**
     * 역인덱스 일괄 삭제
     */
    @Override
    @Transactional
    public void deleteIndexes(Set<String> indexKeys) {
        reverseIndexRepository.deleteAllById(indexKeys);
        log.debug("[ReverseIndex] deleteIndexes: {}개", indexKeys.size());
    }
}
