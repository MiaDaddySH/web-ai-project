# Tlias Web Management

English | [简体中文](README.zh-CN.md)

Tlias Web Management is a course-based learning project for building a full-stack web application. The course covers backend development with Spring Boot, MySQL, MyBatis, and JWT, together with frontend development using Vue.

The code in this repository is based on course examples and assignments. While following the course, I also refactor and improve implementations that can be made clearer, safer, or easier to maintain.

> This is an educational project under active development. It is not intended to be a production-ready application.

## Current implementation

The repository currently contains the backend application. Its implemented features include:

- Department CRUD operations
- Paginated employee queries
- Optional employee filters by name, gender, and entry-date range
- A Controller–Service–Mapper layered architecture
- MyBatis dynamic SQL mappings
- Unified API response and pagination models
- Console logging with Logback
- Maven-based tests and GitHub Actions CI

Vue-based frontend development and JWT authentication are part of the course scope and may be added as the project progresses.

## Technology stack

### Currently used

- Java 17
- Spring Boot 4.0.8
- Spring Web MVC
- MyBatis 4.0.1
- MySQL
- PageHelper
- Lombok
- Logback
- Maven Wrapper

### Course scope

- JWT authentication
- Vue frontend development

## Project structure

```text
web-ai-project/
├── .github/
│   └── workflows/              # GitHub Actions workflows
├── tlias-web-management/       # Spring Boot backend
│   ├── .mvn/                   # Maven Wrapper configuration
│   ├── src/main/java/          # Application source code
│   │   └── com/sheng/
│   │       ├── controller/     # HTTP API layer
│   │       ├── mapper/         # MyBatis mapper interfaces
│   │       ├── pojo/           # Domain and response models
│   │       └── service/        # Business logic layer
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML mappings
│   │   ├── application.yml     # Application configuration
│   │   └── logback.xml         # Logging configuration
│   ├── src/test/java/          # Tests
│   ├── .env.example            # Local configuration template
│   └── pom.xml
├── README.md                   # English documentation (default)
└── README.zh-CN.md             # Chinese documentation
```

## Getting started

### Prerequisites

- JDK 17 or later
- MySQL 8.x

The Maven Wrapper is included, so a separate Maven installation is not required.

### Database configuration

From the repository root, create the local environment file:

```bash
cp tlias-web-management/.env.example tlias-web-management/.env
```

Update `tlias-web-management/.env` with your local database settings:

```properties
DB_URL=jdbc:mysql://localhost:3306/tlias
DB_USERNAME=root
DB_PASSWORD=your_password
```

The `.env` file is ignored by Git. Do not put real credentials in `.env.example` or any other tracked file.

The project currently expects an existing `tlias` database and the tables used by the course exercises.

### Run the application

Run the following command from the repository root so that the configured `.env` path can be resolved:

```bash
./tlias-web-management/mvnw \
  -f tlias-web-management/pom.xml \
  spring-boot:run
```

On Windows:

```powershell
tlias-web-management\mvnw.cmd `
  -f tlias-web-management\pom.xml `
  spring-boot:run
```

The application is available at `http://localhost:8080` by default.

## API overview

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/depts` | List all departments |
| `GET` | `/depts/{id}` | Get a department by ID |
| `POST` | `/depts` | Create a department |
| `PUT` | `/depts` | Update a department |
| `DELETE` | `/depts?id={id}` | Delete a department |
| `GET` | `/emps` | Query employees with pagination and optional filters |

Employee query parameters:

| Parameter | Required | Default | Description |
| --- | --- | --- | --- |
| `page` | No | `1` | Page number |
| `pageSize` | No | `10` | Number of records per page |
| `name` | No | — | Partial employee-name match |
| `gender` | No | — | Gender value used by the course data model |
| `begin` | No | — | Earliest entry date in `yyyy-MM-dd` format |
| `end` | No | — | Latest entry date in `yyyy-MM-dd` format |

Example:

```text
GET /emps?page=1&pageSize=10&name=Lin&gender=1&begin=2024-01-01&end=2026-12-31
```

## Test and build

Run these commands from the repository root:

```bash
./tlias-web-management/mvnw -f tlias-web-management/pom.xml test
./tlias-web-management/mvnw -f tlias-web-management/pom.xml package
```

GitHub Actions runs the Maven verification workflow for pull requests and pushes to the `main` branch.

## Configuration

| Variable | Purpose | Example |
| --- | --- | --- |
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/tlias` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | Store only in the local `.env` file |

## Project status

The project evolves alongside the course. Features, tests, documentation, and implementation improvements will be added incrementally as new topics are covered.
