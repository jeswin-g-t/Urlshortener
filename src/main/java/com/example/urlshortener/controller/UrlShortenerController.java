package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Value("${app.base-url:}")
    private String configuredBaseUrl;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<Resource> home() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(new ClassPathResource("static/index.html"));
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> request, HttpServletRequest servletRequest) {
        String originalUrl = request.get("url");
        if (originalUrl == null || originalUrl.isBlank() || !service.isValidUrl(originalUrl)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid URL. Please enter a valid URL to shorten."));
        }

        String shortCode = service.createShortUrl(originalUrl);
        String baseUrl = resolveBaseUrl(servletRequest);
        String shortUrl = baseUrl + "/" + shortCode;

        return ResponseEntity.ok(Map.of(
                "shortCode", shortCode,
                "shortUrl", shortUrl,
                "originalUrl", originalUrl
        ));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        Optional<String> originalUrl = service.getOriginalUrl(shortCode);
        if (originalUrl.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl.get()));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body("<h2>404 - Short URL Not Found</h2><p>The shortened link you requested does not exist or has expired.</p><a href='/'>Create a new link</a>");
    }

    private String resolveBaseUrl(HttpServletRequest request) {
        if (configuredBaseUrl != null && !configuredBaseUrl.isBlank() && !configuredBaseUrl.contains("localhost")) {
            return configuredBaseUrl.replaceAll("/+$", "");
        }

        // Dynamically detect scheme and host from the client's request
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isBlank()) {
            scheme = request.getScheme();
        }

        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getHeader("Host");
        }

        if (host != null && !host.isBlank()) {
            return scheme + "://" + host;
        }

        return "http://localhost:" + request.getServerPort();
    }
}