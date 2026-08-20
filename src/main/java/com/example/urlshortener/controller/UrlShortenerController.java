package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<Resource> home() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(new ClassPathResource("static/index.html"));
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("url");
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL must not be empty"));
        }
        String shortCode = service.createShortUrl(originalUrl);
        return ResponseEntity.ok(Map.of("shortUrl", baseUrl + "/" + shortCode));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Optional<String> originalUrl = service.getOriginalUrl(shortCode);
        if (originalUrl.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl.get()));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        return ResponseEntity.notFound().build();
    }
}