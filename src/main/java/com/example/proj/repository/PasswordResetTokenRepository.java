package com.example.proj.repository;

import com.example.proj.model.passwordResetToken.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken,String> {
    @Query("{'token': ?0}")
    PasswordResetToken findByToken(String token);
}
