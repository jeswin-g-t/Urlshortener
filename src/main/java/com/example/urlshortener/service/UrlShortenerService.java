package com.example.urlshortener.service;

import com.example.urlshortener.model.ShortenedUrl;
import com.example.urlshortener.repository.ShortenedUrlRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private final ShortenedUrlRepository repository;

    public UrlShortenerService(ShortenedUrlRepository repository) {
        this.repository = repository;
    }

    public String createShortUrl(String originalUrl) {
        String shortCode = generateShortCode();
        ShortenedUrl shortenedUrl = new ShortenedUrl(originalUrl, shortCode);
        repository.save(shortenedUrl);
        return shortCode;
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode).map(ShortenedUrl::getOriginalUrl);
    }

    private String generateShortCode() {
        String uuid = UUID.randomUUID().toString();
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(uuid.getBytes()).substring(0, 8);
        while (repository.findByShortCode(code).isPresent()) {
            uuid = UUID.randomUUID().toString();
            code = Base64.getUrlEncoder().withoutPadding().encodeToString(uuid.getBytes()).substring(0, 8);
        }
        return code;
    }
}