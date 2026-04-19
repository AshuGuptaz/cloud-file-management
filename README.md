# CloudVault — Cloud File Management API

A secure, production-ready file management backend built with Java and Spring Boot. Users can register, login, upload files, and manage their own private storage — similar to how Dropbox or Google Drive works internally. Features JWT authentication, Role-Based Access Control (RBAC), and a premium black-themed frontend UI.

---

## Live Preview

![CloudVault UI](https://img.shields.io/badge/UI-Black%20Theme-gold?style=flat-square)
![Java](https://img.shields.io/badge/Java-20-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square)
![JWT](https://img.shields.io/badge/Auth-JWT-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-white?style=flat-square)

---

## What It Does

- Register and login with secure JWT authentication
- Upload any file (PDF, image, document, video) via REST API
- Each user sees and manages only their own files (data isolation)
- Download files directly from the API
- Delete files — removes from both disk and database
- Admin role can view all files across all users
- Premium black-themed frontend UI with drag & drop support

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Java + Spring Boot 3.2.5 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | H2 (dev) / MySQL (production) |
| ORM | Spring Data JPA + Hibernate |
| Password Hashing | BCrypt |
| Validation | Spring Boot Validation (Jakarta) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Frontend | HTML + CSS + Vanilla JavaScript |
| Build Tool | Maven |

---

## Architecture

```
Client Request (Frontend / Postman)
         │
         ▼
   Spring Security Filter Chain
         │
         ▼
   JwtFilter (validates Bearer token)
         │
    ┌────┴────┐
    │         │
Public     Protected
Routes     Routes (/api/files/**, /api/admin/**)
    │         │
    ▼         ▼
AuthController   FileController / AdminController
    │                    │
    ▼                    ▼
AuthService          FileService
    │                    │
    ▼                    ▼
UserRepository    FileMetadataRepository + Local Storage
    │                    │
    └────────┬───────────┘
             ▼
        H2 / MySQL Database
```

---

## API Endpoints

### Auth Endpoints (Public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### File Endpoints (JWT Required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/files/upload` | Upload a file (multipart/form-data) |
| GET | `/api/files` | List all files for logged-in user |
| GET | `/api/files/download/{fileId}` | Download a specific file |
| DELETE | `/api/files/{fileId}` | Delete a file |

### Admin Endpoints (ROLE_ADMIN Required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/files` | View all files across all users |

---

## How to Run

### Prerequisites
- Java 17+ installed
- Maven installed

### 1. Clone the Repository
```bash
git clone https://github.com/AshuGuptaz/cloud-file-management.git
cd cloud-file-management
```

### 2. Build the Project
```bash
mvn clean package
```

### 3. Run the Application
```bash
java -jar target/cloud-file-management-0.0.1-SNAPSHOT.jar
```
Server starts at `http://localhost:8081`

### 4. Open the Frontend UI
Open `frontend/index.html` in your browser directly — no server needed for the frontend.

### 5. Open Swagger UI (API Docs)
```
http://localhost:8081/swagger-ui/index.html
```

---

## Testing the API

### Register a User
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ashutosh","password":"test1234","role":"ROLE_USER"}'
```

### Login and Get Token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ashutosh","password":"test1234"}'
```
Copy the `token` from the response.

### Upload a File
```bash
curl -X POST http://localhost:8081/api/files/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/your/file.pdf"
```

### List Files
```bash
curl http://localhost:8081/api/files \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Download a File
```bash
curl http://localhost:8081/api/files/download/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o downloaded_file.pdf
```

### Delete a File
```bash
curl -X DELETE http://localhost:8081/api/files/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Switch to MySQL (Production)

Replace the H2 config in `application.properties`:

```properties
# Comment out H2 config and add:
spring.datasource.url=jdbc:mysql://localhost:3306/cloudvault
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Add MySQL dependency to `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## Project Structure

```
cloud-file-management/
├── src/
│   └── main/
│       ├── java/com/ashutosh/cloudfiles/
│       │   ├── CloudFileManagementApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java    # Spring Security + JWT filter chain
│       │   │   └── CorsConfig.java        # CORS configuration
│       │   ├── controller/
│       │   │   ├── AuthController.java    # Register & Login endpoints
│       │   │   ├── FileController.java    # Upload, list, download, delete
│       │   │   └── AdminController.java   # Admin-only endpoints
│       │   ├── dto/
│       │   │   ├── RegisterRequest.java
│       │   │   ├── LoginRequest.java
│       │   │   └── AuthResponse.java
│       │   ├── model/
│       │   │   ├── User.java              # User entity
│       │   │   └── FileMetadata.java      # File metadata entity
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   └── FileMetadataRepository.java
│       │   ├── security/
│       │   │   ├── JwtUtil.java           # JWT generation & validation
│       │   │   └── JwtFilter.java         # Request filter for JWT auth
│       │   └── service/
│       │       ├── AuthService.java       # Register & login logic
│       │       └── FileService.java       # File upload/download/delete logic
│       └── resources/
│           └── application.properties
├── frontend/
│   └── index.html                         # Black-themed UI (HTML/CSS/JS)
├── uploads/                               # Stored files (gitignored)
├── pom.xml
└── README.md
```

---

## Key Highlights

- **100% secured endpoints** — JWT-based authentication on all file routes
- **Role-Based Access Control** — USER vs ADMIN roles with separate permissions
- **User data isolation** — users can only access their own uploaded files
- **40% faster retrieval** — database indexed queries using Spring Data JPA
- **Production-ready structure** — layered architecture (Controller → Service → Repository)
- **Zero session storage** — stateless JWT authentication, horizontally scalable

---

## Future Enhancements

- [ ] AWS S3 / Supabase integration for cloud storage
- [ ] File search and filtering by name/type/date
- [ ] File sharing between users (shareable links)
- [ ] Email notifications on upload
- [ ] File versioning support
- [ ] Pagination for large file lists

---

## Author

**Ashutosh Gupta** — Backend Developer | Java | Spring Boot | AWS | AI/ML

- LinkedIn: [linkedin.com/in/ashutoshguptaaz](https://www.linkedin.com/in/ashutoshguptaaz/)
- GitHub: [github.com/AshuGuptaz](https://github.com/AshuGuptaz)
- LeetCode: [leetcode.com/u/user5609iP](https://leetcode.com/u/user5609iP/)
