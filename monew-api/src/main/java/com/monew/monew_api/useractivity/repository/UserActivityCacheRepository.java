package com.monew.monew_api.useractivity.repository;

import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityCacheRepository extends MongoRepository<UserActivityCacheDocument, String>, UserActivityCacheCustomRepository {
}