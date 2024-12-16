package com.example.proj.repository;

import com.example.proj.model.verificationToken.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken,String> {

    @Query("{'token': ?0}")
    VerificationToken findByToken(String token);
}
