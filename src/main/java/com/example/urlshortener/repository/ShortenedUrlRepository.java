package com.example.urlshortener.repository;

import com.example.urlshortener.model.ShortenedUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ShortenedUrlRepository extends MongoRepository<ShortenedUrl, String> {
    Optional<ShortenedUrl> findByShortCode(String shortCode);
}