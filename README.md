# User Service API

**Production-grade JWT based authentication 'user' microservice for e-commerce platform**

## 🚀 Quick Start

```bash
# Clone & build
git clone <repo>
cd user-service
mvn clean install

# Run (Redis + Mysql required)
mvn spring-boot:run

# Default credentials
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sbcharr@shop.com","password":"secret123"}'
```

## 🔑 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/v1/auth/signup` | None | Register new user |
| `POST` | `/api/v1/auth/login` | None | JWT login |
| `PUT` | `/api/v1/auth/logout` | JWT | Blacklist token |
| `PUT` | `/api/v1/auth/verify-email?token=...` | None | Email verification |
| `GET` | `/api/v1/users/profile` | JWT | Get user profile |
| `GET` | `/api/v1/auth/sample` | None | Service health ping |

## 🛡️ Security

- **JWT**: HS256, 15min expiry, Redis based blacklist
- **Soft-delete**: `deletedAt` timestamp
- **BCrypt**: Strength 16
- **Roles**: `BUYER`, `MERCHANT`, `ADMIN`

## 🧪 Health Checks

```bash
/actuator/health/liveness      # K8s liveness
/actuator/health/readiness     # K8s readiness  
/api/v1/auth/sample            # Service ping
```

## 🏗️ Architecture

```
Client → JWT Filter → UserService → Redis/DB
   ↓       ↓            ↓            ↓
Validate  Claims     BCrypt       Blacklist
```

**Features:** fast auth, stateless profile, validation-first API

## 📦 Dependencies

```
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-data-redis  
spring-boot-starter-validation
jjwt-impl
lombok
mysql
```
