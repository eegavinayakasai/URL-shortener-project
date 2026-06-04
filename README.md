# 🔗 Secure URL Shortener API

A production-style URL Shortener backend built using Spring Boot. This project allows users to create short URLs, generate QR codes, track click analytics, and manage their URLs securely using JWT Authentication and Email OTP Verification.

## 🚀 Features

### URL Management

* Create short URLs from long URLs
* Custom alias support
* Automatic URL redirection
* URL expiration handling
* Click tracking and analytics
* User-specific URL history

### Security

* JWT-based Authentication
* User Registration & Login
* Email OTP Verification
* Protected API Endpoints
* Password Encryption using Spring Security

### Additional Features

* QR Code Generation
* Global Exception Handling
* Request Validation
* Swagger/OpenAPI Documentation

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

### Authentication

* JWT (JSON Web Token)
* Email OTP Verification

### Tools

* Maven
* Swagger/OpenAPI
* Lombok

---

## 📂 Project Structure

```text
src/main/java
├── controller
├── service
├── repository
├── model
├── dto
├── security
├── exception
└── exceptionHandler
```

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

| Method | Endpoint        | Description                        |
| ------ | --------------- | ---------------------------------- |
| POST   | /api/shorten    | Create a short URL                 |
| GET    | /api/{code}     | Redirect to original URL           |
| GET    | /api/stats      | Get URL statistics                 |
| GET    | /api/my-urls    | Get URLs created by logged-in user |
| POST   | /api/generateQr | Generate QR code                   |

---

## 📊 Example Flow

1. Request OTP using email
2. Verify OTP
3. Register account
4. Login and obtain JWT token
5. Create short URLs
6. Access protected endpoints using JWT
7. Track URL analytics and click counts

---

## 🔐 Security

* Passwords are encrypted before storage.
* JWT is used for stateless authentication.
* Protected endpoints require a valid Bearer Token.
* Email verification is required during registration.

---

## 📈 Future Improvements

* Deploy to Cloud (Render/AWS/Azure)
* Redis Caching
* Rate Limiting
* Docker Containerization
* Unit & Integration Testing
* Custom Expiration Configuration
* Detailed Click Analytics

---

## 🧪 Running Locally

### Clone Repository

```bash
git clone <your-repository-url>
cd <repository-name>
```

### Configure Database

Update your application.properties:

```properties
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

jwt.secret=YOUR_SECRET_KEY
jwt.expiry=86400000
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

## 👨‍💻 Author

Vinayaka Sai

Java Backend Developer

Focused on building secure and scalable backend applications using Java, Spring Boot, Spring Security, and MySQL.
