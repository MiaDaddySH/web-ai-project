# Web AI Project

一个基于 Spring Boot、Spring MVC、MyBatis 和 MySQL 的 Web 管理系统后端项目。当前仓库已完成基础分层结构搭建，后续将在此基础上实现部门等业务模块。

## 技术栈

- Java 17
- Spring Boot 4.0.8
- Spring MVC
- MyBatis 4.0.1
- MySQL
- Maven Wrapper
- Lombok

## 项目结构

```text
web-ai-project/
├── .github/workflows/       # GitHub Actions 持续集成
└── tlias-web-management/    # 后端应用
    ├── .mvn/                # Maven Wrapper 配置
    ├── src/main/java/       # 业务代码
    ├── src/main/resources/  # 应用配置
    ├── src/test/java/       # 测试代码
    └── pom.xml
```

后端采用 Controller、Service、Mapper 分层结构。当前业务类为基础骨架，具体接口仍在开发中。

## 本地运行

### 1. 环境要求

- JDK 17 或更高版本
- MySQL 8.x

项目提供 Maven Wrapper，无需单独安装 Maven。

### 2. 配置数据库

先创建本地配置文件：

```bash
cd tlias-web-management
cp .env.example .env
```

然后编辑 `.env`：

```properties
DB_URL=jdbc:mysql://localhost:3306/tlias
DB_USERNAME=root
DB_PASSWORD=your_password
```

`.env` 仅用于本地，已加入 `.gitignore`，不会被提交到仓库。不要在 `.env.example` 或其他受 Git 管理的文件中填写真实密码。

### 3. 启动应用

```bash
./mvnw spring-boot:run
```

Windows：

```powershell
mvnw.cmd spring-boot:run
```

默认服务地址为 `http://localhost:8080`。

如果不从后端目录启动，可通过 `ENV_FILE` 指定配置文件：

```bash
ENV_FILE=/absolute/path/to/.env ./mvnw spring-boot:run
```

## 测试与构建

```bash
cd tlias-web-management
./mvnw test
./mvnw package
```

GitHub Actions 会在推送到 `main` 分支以及创建 Pull Request 时自动执行构建和测试。

## 配置说明

| 配置项 | 用途 | 示例 |
| --- | --- | --- |
| `DB_URL` | JDBC 连接地址 | `jdbc:mysql://localhost:3306/tlias` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | 请仅写入本地 `.env` |
| `ENV_FILE` | 可选的 `.env` 文件路径 | `/path/to/.env` |

## 开发状态

项目处于早期开发阶段。Controller、Service 和 Mapper 分层已经建立，业务接口、数据库迁移脚本以及更完整的单元测试和集成测试将随功能实现逐步补充。
