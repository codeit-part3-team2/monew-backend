package com.monew.monew_api.useractivity.service;

import java.util.Set;

public interface ReverseIndexService {
    void addUser(String indexKey, String userId);
    void removeUser(String indexKey, String userId);
    Set<String> getUserIds(String indexKey);
    Set<String> getUserIds(Set<String> indexKeys);
    void deleteIndexes(Set<String> indexKeys);
}
