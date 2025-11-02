package com.monew.monew_api.useractivity.repository;

import java.util.Set;

public interface ReverseIndexCustomRepository {
    void addUser(String indexKey, String userId);

    void removeUser(String indexKey, String userId);

    Set<String> findUserIdsByKeys(Set<String> indexKeys);
}
