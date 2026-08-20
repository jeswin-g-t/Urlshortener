# 🔗 URL Shortener

A full-stack URL shortener built with **Java Spring Boot** and **MongoDB**. It converts long, unwieldy URLs into short, shareable links and redirects users to the original URL when the short link is visited.

## 📌 Features

- Convert any long URL into a short, unique code
- Fast redirection from short URL to original URL
- Duplicate-free short code generation (UUID + Base64)
- Simple, responsive web UI with animated gradient background
- MongoDB-backed persistent storage
- Basic input validation and error handling (404 for unknown codes)

## 🛠️ Tech Stack

| Layer          | Technology                     |
|----------------|---------------------------------|
| Backend        | Java 17, Spring Boot 4.x        |
| Database       | MongoDB (Spring Data MongoDB)   |
| Frontend       | HTML, CSS, JavaScript           |
| Build Tool     | Maven                           |
| IDE            | VS Code (Spring Boot Extension Pack) |

## 🏗️ Architecture

The project follows a layered architecture:

- **Controller** – Handles HTTP requests (`/shorten`, `/{shortCode}`)
- **Service** – Business logic: generates unique short codes, coordinates storage
- **Repository** – Spring Data MongoDB interface for CRUD operations
- **Model** – `ShortenedUrl` entity mapped to the `shortened_urls` collection

## ⚙️ Setup & Installation

### Prerequisites
- Java JDK 17+
- Maven
- MongoDB (local instance or MongoDB Atlas)
- VS Code with Java + Spring Boot extensions (recommended)

### Steps

1. **Clone the repository**
```bash
   git clone <your-repo-url>
   cd urlshortener
```

2. **Configure MongoDB connection**
   In `src/main/resources/application.properties`:
```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/urlshortener
   server.port=8080
   app.base-url=http://localhost:8080
```

3. **Run the application**
```bash
   mvn spring-boot:run
```
   Or run `UrlshortenerApplication.java` directly from VS Code.

4. **Open in browser**


## Future Enhancements:
- User login/registration for managing personal links
- Link expiry with configurable expiration times
- Custom short codes chosen by the user
- QR code generation for each short URL
- Mobile app version (Android/iOS)
