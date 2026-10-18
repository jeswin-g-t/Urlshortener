package com.example.urlshortener.service;

import com.example.urlshortener.model.ShortenedUrl;
import com.example.urlshortener.repository.ShortenedUrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UrlShortenerService {

    // Base62 characters: a-z, A-Z, 0-9 (62 characters)
    // 6 characters length gives 62^6 = 56,800,235,584 (~56.8 billion) combinations
    private static final String BASE62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortenedUrlRepository repository;

    public UrlShortenerService(ShortenedUrlRepository repository) {
        this.repository = repository;
    }

    public String createShortUrl(String originalUrl) {
        String normalizedUrl = normalizeUrl(originalUrl);
        String shortCode = generateUniqueBase62Code();
        ShortenedUrl shortenedUrl = new ShortenedUrl(normalizedUrl, shortCode);
        repository.save(shortenedUrl);
        return shortCode;
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode).map(ShortenedUrl::getOriginalUrl);
    }

    public boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String candidate = url.trim();
        if (!candidate.startsWith("http://") && !candidate.startsWith("https://")) {
            candidate = "https://" + candidate;
        }
        try {
            java.net.URI uri = new java.net.URI(candidate);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                return false;
            }
            if (host.contains("..") || host.startsWith(".") || host.endsWith(".")) {
                return false;
            }
            if (!host.contains(".") && !host.equalsIgnoreCase("localhost")) {
                return false;
            }
            if (!host.equalsIgnoreCase("localhost") && !host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                String[] parts = host.split("\\.");
                String tld = parts[parts.length - 1];
                if (!tld.matches("[a-zA-Z]{2,}")) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeUrl(String url) {
        String trimmed = url.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    private String generateUniqueBase62Code() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                int index = RANDOM.nextInt(BASE62_CHARS.length());
                sb.append(BASE62_CHARS.charAt(index));
            }
            code = sb.toString();
        } while (repository.findByShortCode(code).isPresent());
        return code;
    }
}