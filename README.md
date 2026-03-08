# Smart Contact Manager Backend

## ⚙️ Smart Contact Manager Backend

### 🧩 Overview
- ⚡ **Backend:** Spring Boot 4 + Java 21 + PostgreSQL  
- 🔐 **Authentication:** JWT + Google OAuth2  
- 🐳 **Deployment Ready:** Docker + Render  
- 📧 **Email Provider:** Resend (HTTP API)

---

## 🚀 Core Capabilities

### 👤 User Authentication & Account
- ✉️ Register / Login with email & password  
- 🔑 Google OAuth login  
- 👤 Get and update user profile  
- 🔒 Set or change password  
- 🗑 Delete user account  

---

### 🛡 Verification & Recovery
- 📧 Email verification token flow  
- 🔢 Forgot password OTP system  
- 🔁 Reset password via OTP  

---

### 📇 Contact Management
- ➕ Create contacts  
- 📋 List all contacts  
- 🔎 Search contacts  
- ✏️ Update contacts  
- ❌ Delete contacts  
- ⭐ Mark favourite contacts  
- 📊 Contact statistics  
- 🌐 Store social links and full contact details  

---

## 🔌 API Surface

- 🌍 **Total REST Endpoints:** `21`

### Controllers
- 👤 **User Controller:** 11 endpoints  
- 📇 **Contact Controller:** 9 endpoints  
- ❤️ **Health / Startup Controller:** 1 endpoint  

---

## 🔐 Security

- 🪪 JWT-based authorization (`sub = userId`)  
- 🛡 Spring Security route protection  
- 🔑 BCrypt password hashing  
- 🔗 OAuth2 login success handler for frontend callback  
- 🌐 CORS allowlist via configuration  

---

## ⚡ Performance Optimizations

- 📌 Contact queries switched to **userId-based access**
- 🚫 Removed redundant query after contact creation

### Database Indexing
- `users.email` → unique + indexed  
- `contact.user_user_id`  
- `contact(user_user_id, favourite)`  
- `contact(user_user_id, name)`

### Lazy Loading Enabled
- `User.emailVerificationToken`  
- `Contact.socialLinks`

---

## 🗄 Database & ORM

- 🐘 PostgreSQL with JPA / Hibernate  
- 🔐 Ownership-safe queries (`findByIdAndUserUserId`)  
- ⚡ Reduced unnecessary joins and entity loading  

---

## 📧 Email Architecture

- 🔄 Migrated from **Gmail SMTP → Resend API**
- ⚡ Async email sending with failure logging  
- 📩 One Resend API call per email sent  

---

## 🐳 DevOps & Deployment

- 🐳 Dockerized backend  
- ⚙️ Environment-driven configuration (`.env`, Render env vars)  
- 🌐 Render-compatible mail strategy (HTTPS instead of SMTP ports)

---

## 🔑 Required Mail Environment Variables


Spring Boot backend for Smart Contact Manager.

## Tech Stack
- Java 21
- Spring Boot 4
- Maven Wrapper (`./mvnw`)
- PostgreSQL 16
- Docker + Docker Compose

## Prerequisites

### For Docker run
- Docker
- Docker Compose

### For direct local run
- Java 21
- PostgreSQL (local)

## Environment Setup
1. Copy environment file:
```bash
cp .env.example .env
```
2. Update values in `.env`.

Required variables:
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `OAUTH2_REDIRECT_URI`

For direct run, also export DB variables used by Spring:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Example for local PostgreSQL:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/smart_contact_manager
export DB_USERNAME=scm_user
export DB_PASSWORD=scm_password
```

## Run with Docker (recommended)
Build and start all services:
```bash
docker compose up --build -d
```

Check logs:
```bash
docker compose logs -f app
```

Stop services:
```bash
docker compose down
```

App URL:
- `http://localhost:8080`

## Run Directly (without Docker)
1. Make sure PostgreSQL is running and database/user exist.
2. Load env vars:
```bash
set -a
source .env
set +a
```
3. Export DB vars expected by Spring:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/$POSTGRES_DB
export DB_USERNAME=$POSTGRES_USER
export DB_PASSWORD=$POSTGRES_PASSWORD
```
4. Start app:
```bash
./mvnw spring-boot:run
```

Alternative (jar):
```bash
./mvnw clean package -DskipTests
java -jar target/smart-contact-manager-backend-0.0.1-SNAPSHOT.jar
```

## Git Workflow
Clone:
```bash
git clone <your-repo-url>
cd smart-contact-manager-backend
```

Create branch:
```bash
git checkout -b feature/<name>
```

Sync latest main:
```bash
git checkout main
git pull origin main
git checkout feature/<name>
git rebase main
```

Commit and push:
```bash
git add .
git commit -m "feat: <short message>"
git push -u origin feature/<name>
```

## Useful Commands
Run tests:
```bash
./mvnw test
```

Rebuild Docker images:
```bash
docker compose build --no-cache
```

## Notes
- App listens on port `8080`.
- JPA is currently set to `spring.jpa.hibernate.ddl-auto=create` (data resets on restart).
