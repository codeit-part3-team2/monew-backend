package com.monew.monew_api.useractivity.repository;

import com.monew.monew_api.useractivity.document.ReverseIndexDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReverseIndexRepository extends MongoRepository<ReverseIndexDocument, String>, ReverseIndexCustomRepository {
}
