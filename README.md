# Tlias Web Management

English | [简体中文](README.zh-CN.md)

Tlias Web Management is a course project for learning how to build a full-stack web application. The course covers backend development with Spring Boot, MySQL, MyBatis, and JWT, as well as frontend development with Vue.

The code in this repository comes from course examples and assignments. I also refactor and improve parts of the course code to make them clearer, safer, and easier to maintain.

> This project is for learning only. It is not ready for production use.

## Project status

The backend is close to completion. The main business features, login and authentication, reports, operation logs, and file upload are available. The Vue frontend is not included yet and will be added later.

## Features

- Login and JWT-based authentication
- Department management
- Employee management with pagination and optional filters
- Class management with pagination and optional filters
- Student management, including batch deletion and violation-score updates
- Employee and student statistics
- Local image upload with file type validation
- Annotation-based operation logging with Spring AOP
- Paginated operation-log queries
- Unified API response and pagination models
- MyBatis dynamic SQL mappings
- Global exception handling
- Maven multi-module project structure
- Maven tests and GitHub Actions CI
- A separate custom Aliyun OSS Spring Boot Starter example

## Technology stack

- Java 17
- Spring Boot 4.0.8
- Spring Web MVC
- Spring AOP
- MyBatis 4.0.1
- MySQL 8.x
- PageHelper
- JWT (`jjwt`)
- Lombok
- Logback
- Maven Wrapper and Maven multi-module builds
- Aliyun OSS SDK in the custom starter example
- Vue (planned frontend)

## Project structure

```text
web-ai-project/
├── .github/workflows/                  # GitHub Actions workflows
├── tlias-parent/                       # Parent POM and backend module list
├── tlias-pojo/                         # Domain, query, and response models
├── tlias-utils/                        # Shared utilities, including JWT support
├── tlias-web-management/               # Main Spring Boot backend application
│   ├── src/main/java/com/sheng/
│   │   ├── anno/                       # Custom annotations
│   │   ├── aop/                        # Operation-log aspect
│   │   ├── config/                     # Web configuration
│   │   ├── controller/                 # REST controllers
│   │   ├── exception/                  # Application exceptions and handlers
│   │   ├── interceptor/                # JWT authentication interceptor
│   │   ├── mapper/                     # MyBatis mapper interfaces
│   │   └── service/                    # Business services
│   ├── src/main/resources/
│   │   ├── mapper/                     # MyBatis XML mappings
│   │   ├── application.yml             # Application configuration
│   │   └── logback.xml                 # Logging configuration
│   ├── src/test/java/                  # Tests
│   └── .env.example                    # Local configuration template
├── aliyun-oss-spring-boot-autoconfigure/ # Aliyun OSS auto-configuration
├── aliyun-oss-spring-boot-starter/     # Custom Aliyun OSS starter
├── springboot-autoconfiguration-test/  # Starter test application
├── README.md                           # English documentation (default)
└── README.zh-CN.md                     # Chinese documentation
```

## Getting started

### Requirements

- JDK 17 or later
- MySQL 8.x

The Maven Wrapper is included, so Maven does not need to be installed separately.

### Database and local configuration

Create an empty `tlias` database before starting the application. Flyway manages the schema and demo data with the versioned migrations in `tlias-web-management/src/main/resources/db/migration`.

```sql
CREATE DATABASE tlias
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
```

On the first startup, Flyway creates the application tables and inserts the demo data automatically. The demo login is `songjiang` / `123456`.

If you already have a manually initialized, non-empty `tlias` database, back it up and define a Flyway baseline before enabling these migrations. Do not apply the initial migrations directly to an existing schema.

From the repository root, create a local environment file:

```bash
cp tlias-web-management/.env.example tlias-web-management/.env
```

Then update `tlias-web-management/.env`:

```properties
DB_URL=jdbc:mysql://localhost:3306/tlias
DB_USERNAME=root
DB_PASSWORD=your_password
UPLOAD_DIR=uploads
```

The `.env` file is ignored by Git. Do not put real passwords or other secrets in `.env.example` or any tracked file.

### Build the backend modules

Run this command from the repository root:

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw -f tlias-parent/pom.xml clean install
```

On Windows:

```powershell
$env:ENV_FILE = "$PWD\tlias-web-management\.env"
tlias-web-management\mvnw.cmd -f tlias-parent\pom.xml clean install
```

This builds the shared model and utility modules before the main application. `ENV_FILE` is set explicitly because Maven runs each module from its own directory.

### Run the application

After the backend modules have been built, run:

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw \
  -f tlias-web-management/pom.xml \
  spring-boot:run
```

On Windows:

```powershell
$env:ENV_FILE = "$PWD\tlias-web-management\.env"
tlias-web-management\mvnw.cmd `
  -f tlias-web-management\pom.xml `
  spring-boot:run
```

The application is available at `http://localhost:8080` by default.

## Authentication

`POST /login` is public. Other endpoints require a valid JWT in the `token` request header:

```http
token: your_jwt_token
```

## API overview

| Module | Main endpoints | Description |
| --- | --- | --- |
| Login | `POST /login` | Check login details and return a JWT |
| Departments | `/depts`, `/depts/{id}` | List, create, read, update, and delete departments |
| Employees | `/emps`, `/emps/list`, `/emps/{id}` | Paginated queries, filters, and employee CRUD operations |
| Classes | `/clazzs`, `/clazzs/list`, `/clazzs/{id}` | Paginated queries, filters, and class CRUD operations |
| Students | `/students`, `/students/{id}`, `/students/violation/{id}/{score}` | Student CRUD operations and violation-score updates |
| Reports | `/report/*` | Employee job/gender and student class/degree statistics |
| Operation logs | `GET /log/page` | Paginated operation-log query |
| Upload | `POST /upload` | Upload an image to local storage |

Pagination uses `page` and `pageSize`. Employee, class, and student queries also support optional business filters. Date values use the `yyyy-MM-dd` format.

The upload endpoint accepts a multipart field named `file`. Supported extensions are `jpg`, `jpeg`, `png`, `gif`, and `webp`. The default upload directory is `uploads`.

## Configuration

| Variable | Required | Purpose | Example or default |
| --- | --- | --- | --- |
| `DB_URL` | Yes | JDBC connection URL | `jdbc:mysql://localhost:3306/tlias` |
| `DB_USERNAME` | Yes | Database username | `root` |
| `DB_PASSWORD` | Yes | Database password | Set it only in the local `.env` file |
| `UPLOAD_DIR` | No | Local image storage directory | `uploads` |
| `ENV_FILE` | No | Path to another environment file | `./tlias-web-management/.env` |

The application accepts files up to 10 MB. The maximum multipart request size is 100 MB.

## Tests

Run all tests in the main backend modules:

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw -f tlias-parent/pom.xml test
```

A GitHub Actions workflow is included for backend verification on pull requests and pushes to the `main` branch.
The test profile uses H2 and disables Flyway; the MySQL migrations are verified separately against a fresh MySQL database.

## Aliyun OSS starter example

The Aliyun OSS modules are a separate learning example. They show how to create Spring Boot auto-configuration and package it as a custom starter. The main Tlias application currently stores uploaded images on the local file system.

To run the starter test application, install the auto-configuration and starter modules first. Aliyun credentials must be provided through the environment variables supported by the Aliyun SDK, such as `OSS_ACCESS_KEY_ID` and `OSS_ACCESS_KEY_SECRET`. Do not commit these credentials.

## Next steps

- Build and connect the Vue frontend
- Add more unit and integration tests
- Add database initialization scripts or migration support
- Improve API documentation and deployment configuration
