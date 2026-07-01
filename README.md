# 🔗 Secure URL Shortener API

![JUnit 5](https://img.shields.io/badge/JUnit-5-success)
![Mockito](https://img.shields.io/badge/Mockito-Testing-blueviolet)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-success)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Maven](https://img.shields.io/badge/Maven-Build-orange)

A production-style URL Shortener backend built using Spring Boot. This project allows users to create short URLs, generate QR codes, track click analytics, and manage their URLs securely using JWT Authentication and Email OTP Verification.

## 📌 Key Highlights

- 🔐 JWT Authentication with Email OTP Verification
- ⚡ Redis Cache-Aside Pattern reducing response time from ~500–600 ms to under 100 ms
- 📊 Click analytics using Redis with scheduled synchronization to MySQL
- 🧪 Service-layer unit testing using JUnit 5 & Mockito
- 🐳 Docker Compose support for local development

## 🚀 Features

### URL Management

* Create short URLs from long URLs
* Custom alias support
* Automatic URL redirection
* URL expiration handling
* Click tracking and analytics
* User-specific URL history
* Duplicate URL prevention

### Security

* JWT-based Authentication
* User Registration & Login
* Email OTP Verification
* Protected API Endpoints
* Password Encryption using Spring Security

### Performance Optimization

* Redis Cache-Aside Pattern
* Click Counter Tracking using Redis
* Scheduled Synchronization of Click Counts to MySQL
* Database Indexing for Faster Lookups

### Testing

* Unit Testing using JUnit 5
* Mockito for mocking repositories and dependencies
* Service Layer Unit Tests
* Authorization & Exception Path Testing

### Additional Features

* QR Code Generation
* Global Exception Handling
* Request Validation
* Swagger/OpenAPI Documentation
* Docker Compose Support

---

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* MySQL

### Cache

* Redis

### Authentication

* JWT (JSON Web Token)
* Email OTP Verification

### Testing

* JUnit 5
* Mockito

### Tools

* Maven
* Docker
* Docker Compose
* Swagger/OpenAPI
* Lombok
* Postman
* Git & GitHub

---

## 📂 Project Structure

```text
src
├── main
│   └── java
│       ├── controller
│       ├── service
│       ├── repository
│       ├── model
│       ├── dto
│       ├── scheduler
│       ├── security
│       ├── config
│       └── exception
│
└── test
    └── java
        └── service
            └── UrlShortenerServiceTest
```

## 🏗️ Architecture

```text
                Client
                   │
                   ▼
         Spring Boot REST API
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
 Spring Security          Service Layer
      + JWT                    │
                                ▼
                  ┌────────────┴────────────┐
                  ▼                         ▼
               MySQL                     Redis
          (Persistent Data)      (Cache & Click Counter)
```

---

## 🌟 Project Highlights

* Implemented Redis Cache-Aside Pattern to optimize URL lookups.
* Reduced average response time from 500–600 ms to under 100 ms for frequently accessed URLs.
* Stored click counters in Redis and periodically synchronized them with MySQL using Spring Scheduler.
* Added database indexing to improve lookup performance.
* Implemented JWT-based stateless authentication with Email OTP verification.
* Containerized Redis using Docker Compose for simplified development.
* Added comprehensive unit tests using JUnit 5 and Mockito for service-layer business logic.

---

## ⚙️ Setup Instructions

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

## 🧪 Running Tests

Run all unit tests:

```bash
mvn test
```

---

## 📄 License

This project is licensed under the MIT License.


---

## 📌 API Endpoints

### Authentication

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| POST   | /api/auth/register    | Register a new user   |
| POST   | /api/auth/login       | Login and receive JWT |
| POST   | /api/auth/request-otp | Send OTP to email     |
| POST   | /api/auth/verify-otp  | Verify OTP            |

### URL Shortener

| Method | Endpoint                | Description                        |
| ------ | ----------------------- | ---------------------------------- |
| POST   | /api/shorten            | Create a short URL                 |
| GET    | /api/{code}             | Redirect to original URL           |
| GET    | /api/stats              | Get URL statistics                 |
| GET    | /api/my-urls            | Get URLs created by logged-in user |
| POST   | /api/generateQr         | Generate QR code                   |
| DELETE | /api/delete/{shortCode} | Delete a URL                       |
| DELETE | /api/delete/all         | Delete all URLs                    |

---

## 📊 Example Flow

1. Request OTP using email.
2. Verify OTP.
3. Register account.
4. Login and obtain JWT token.
5. Create short URLs.
6. Access protected endpoints using JWT.
7. Redirect using short URLs.
8. Track click analytics and statistics.

---

## 🔐 Security

* Passwords are encrypted before storage.
* JWT is used for stateless authentication.
* Protected endpoints require a valid Bearer Token.
* Email verification is required during registration.

---

## ⚡ Performance Optimization

* Implemented Redis caching using the Cache-Aside pattern.
* Reduced average response time from 500–600 ms to under 100 ms for frequently accessed URLs.
* Click counts are maintained in Redis and periodically synchronized with MySQL using Spring Scheduler.
* Database indexing is used to optimize URL lookups.

---

## ✅ Unit Testing

Service layer unit tests are implemented using **JUnit 5** and **Mockito**.

Covered scenarios include:

* URL creation
* Existing URL detection
* URL redirection
* URL expiration
* Click statistics
* Authorization checks
* Resource not found scenarios
* Delete operations

## 🐳 Running with Docker

Start Redis:

```bash
docker compose up -d
```

Stop Redis:

```bash
docker compose down
```

---

## 🧪 Running Locally

### Clone Repository

```bash
git clone <repository-url>
cd URL-shortener-project
```

### Configure Application

Create:

```text
application.properties
```

using the template provided in:

```text
application-example.properties
```

### Start Redis

```bash
docker compose up -d
```

### Run Application

```bash
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 📈 Future Improvements

* Dockerize the Spring Boot application
* Add MySQL container support
* Implement Rate Limiting
* Introduce OAuth2 Authentication
* Support Custom Domains
* Build an Analytics Dashboard
* Deploy to AWS or Azure

---

## 👨‍💻 Author

### Vinayaka Sai Eega

Java Backend Developer

Focused on building secure and scalable backend applications using Java, Spring Boot, Spring Security, Redis, Docker, and MySQL.
