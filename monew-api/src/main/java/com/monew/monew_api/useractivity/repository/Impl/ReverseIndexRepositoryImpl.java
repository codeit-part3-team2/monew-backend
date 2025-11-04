package com.monew.monew_api.useractivity.repository.Impl;

import com.monew.monew_api.useractivity.document.ReverseIndexDocument;
import com.monew.monew_api.useractivity.repository.ReverseIndexCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReverseIndexRepositoryImpl implements ReverseIndexCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void addUser(String indexKey, String userId) {
        Query q = Query.query(Criteria.where("_id").is(indexKey));
        Update u = new Update()
                .addToSet("userIds", userId)
                .set("updatedAt", LocalDateTime.now());
        mongoTemplate.upsert(q, u, ReverseIndexDocument.class);
    }

    @Override
    public void removeUser(String indexKey, String userId) {
        Query q = Query.query(Criteria.where("_id").is(indexKey));
        Update u = new Update()
                .pull("userIds", userId)
                .set("updatedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(q, u, ReverseIndexDocument.class);
    }

    @Override
    public Set<String> findUserIdsByKeys(Set<String> indexKeys) {
        if (indexKeys.isEmpty()) return Collections.emptySet();
        Query q = Query.query(Criteria.where("_id").in(indexKeys));
        return mongoTemplate.find(q, ReverseIndexDocument.class)
                .stream()
                .flatMap(doc -> doc.getUserIds().stream())
                .collect(Collectors.toSet());
    }
}
