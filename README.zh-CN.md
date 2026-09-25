# Tlias Web Management

[English](README.md) | 简体中文

Tlias Web Management 是一个用于学习全栈 Web 应用开发的课程实践项目。课程内容包括使用 Spring Boot、MySQL、MyBatis 和 JWT 进行后端开发，以及使用 Vue 进行前端开发。

本仓库中的代码来源于课程示例与课程作业。在跟随课程学习的同时，我也会对课程实现中不够理想的部分进行重构和优化，使代码更加清晰、安全且易于维护。

> 本项目仅用于学习，不应被视为可直接用于生产环境的应用。

## 项目状态

后端部分已经接近完成，目前已实现主要业务功能、登录认证、数据统计、操作日志和文件上传。Vue 前端尚未包含在本仓库中，后续将继续开发和接入。

## 已实现功能

- 登录与基于 JWT 的身份认证
- 部门管理
- 员工管理，支持分页和可选条件查询
- 班级管理，支持分页和可选条件查询
- 学生管理，包括批量删除和违纪扣分
- 员工与学生数据统计
- 本地图片上传与文件类型校验
- 基于自定义注解和 Spring AOP 的操作日志
- 操作日志分页查询
- 统一的接口响应与分页结果模型
- MyBatis 动态 SQL 映射
- 全局异常处理
- Maven 多模块项目结构
- Maven 测试及 GitHub Actions 持续集成
- 独立的阿里云 OSS Spring Boot Starter 示例

## 技术栈

- Java 17
- Spring Boot 4.0.8
- Spring Web MVC
- Spring AOP
- MyBatis 4.0.1
- MySQL 8.x
- PageHelper
- JWT（`jjwt`）
- Lombok
- Logback
- Maven Wrapper 与 Maven 多模块构建
- 自定义 Starter 示例中使用的阿里云 OSS SDK
- Vue（计划使用的前端框架）

## 项目结构

```text
web-ai-project/
├── .github/workflows/                  # GitHub Actions 工作流
├── tlias-parent/                       # 父 POM 与后端模块聚合配置
├── tlias-pojo/                         # 领域模型、查询模型和响应模型
├── tlias-utils/                        # 公共工具，包括 JWT 支持
├── tlias-web-management/               # 主要的 Spring Boot 后端应用
│   ├── src/main/java/com/sheng/
│   │   ├── anno/                       # 自定义注解
│   │   ├── aop/                        # 操作日志切面
│   │   ├── config/                     # Web 配置
│   │   ├── controller/                 # REST Controller
│   │   ├── exception/                  # 业务异常与异常处理器
│   │   ├── interceptor/                # JWT 身份认证拦截器
│   │   ├── mapper/                     # MyBatis Mapper 接口
│   │   └── service/                    # 业务服务
│   ├── src/main/resources/
│   │   ├── mapper/                     # MyBatis XML 映射文件
│   │   ├── application.yml             # 应用配置
│   │   └── logback.xml                 # 日志配置
│   ├── src/test/java/                  # 测试代码
│   └── .env.example                    # 本地配置模板
├── aliyun-oss-spring-boot-autoconfigure/ # 阿里云 OSS 自动配置模块
├── aliyun-oss-spring-boot-starter/     # 自定义阿里云 OSS Starter
├── springboot-autoconfiguration-test/  # Starter 测试应用
├── README.md                           # 英文说明（默认）
└── README.zh-CN.md                     # 中文说明
```

## 本地运行

### 环境要求

- JDK 17 或更高版本
- MySQL 8.x

项目已提供 Maven Wrapper，无需单独安装 Maven。

### 数据库与本地配置

启动应用前，请先创建一个空的 `tlias` 数据库。Flyway 会通过 `tlias-web-management/src/main/resources/db/migration` 中的版本化迁移脚本管理表结构和演示数据。

```sql
CREATE DATABASE tlias
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
```

应用第一次启动时，Flyway 会自动创建业务表并写入演示数据。演示登录账号为 `songjiang`，密码为 `123456`。

如果本地已经存在手工初始化的非空 `tlias` 数据库，请先备份数据库并制定 Flyway baseline 方案，不要直接对现有表执行初始迁移。

在仓库根目录执行以下命令，创建本地环境配置文件：

```bash
cp tlias-web-management/.env.example tlias-web-management/.env
```

然后编辑 `tlias-web-management/.env`：

```properties
DB_URL=jdbc:mysql://localhost:3306/tlias
DB_USERNAME=root
DB_PASSWORD=your_password
UPLOAD_DIR=uploads
```

`.env` 已加入 Git 忽略列表。请勿将真实密码或其他密钥写入 `.env.example` 或其他受 Git 管理的文件。

### 构建后端模块

在仓库根目录执行：

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw -f tlias-parent/pom.xml clean install
```

Windows：

```powershell
$env:ENV_FILE = "$PWD\tlias-web-management\.env"
tlias-web-management\mvnw.cmd -f tlias-parent\pom.xml clean install
```

该命令会先构建公共模型和工具模块，然后构建主应用。由于 Maven 会在各模块目录中执行任务，因此这里显式设置了 `ENV_FILE`。

### 启动应用

完成后端模块构建后，执行：

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw \
  -f tlias-web-management/pom.xml \
  spring-boot:run
```

Windows：

```powershell
$env:ENV_FILE = "$PWD\tlias-web-management\.env"
tlias-web-management\mvnw.cmd `
  -f tlias-web-management\pom.xml `
  spring-boot:run
```

应用默认访问地址为 `http://localhost:8080`。

## 身份认证

`POST /login` 无需身份认证。其他接口需要在请求头 `token` 中携带有效的 JWT：

```http
token: your_jwt_token
```

## API 概览

| 模块 | 主要接口 | 说明 |
| --- | --- | --- |
| 登录 | `POST /login` | 校验登录信息并返回 JWT |
| 部门 | `/depts`、`/depts/{id}` | 查询、新增、修改和删除部门 |
| 员工 | `/emps`、`/emps/list`、`/emps/{id}` | 分页、条件查询及员工增删改查 |
| 班级 | `/clazzs`、`/clazzs/list`、`/clazzs/{id}` | 分页、条件查询及班级增删改查 |
| 学生 | `/students`、`/students/{id}`、`/students/violation/{id}/{score}` | 学生增删改查及违纪扣分 |
| 数据统计 | `/report/*` | 员工职位、员工性别、学生班级和学生学历统计 |
| 操作日志 | `GET /log/page` | 分页查询操作日志 |
| 文件上传 | `POST /upload` | 将图片上传到本地存储目录 |

分页接口使用 `page` 和 `pageSize` 参数。员工、班级和学生查询还支持各自的可选业务筛选条件，日期格式为 `yyyy-MM-dd`。

文件上传接口使用名为 `file` 的 multipart 字段，支持 `jpg`、`jpeg`、`png`、`gif` 和 `webp` 扩展名。默认上传目录为 `uploads`。

## 配置说明

| 配置项 | 是否必填 | 用途 | 示例或默认值 |
| --- | --- | --- | --- |
| `DB_URL` | 是 | JDBC 连接地址 | `jdbc:mysql://localhost:3306/tlias` |
| `DB_USERNAME` | 是 | 数据库用户名 | `root` |
| `DB_PASSWORD` | 是 | 数据库密码 | 仅保存在本地 `.env` 文件中 |
| `UPLOAD_DIR` | 否 | 本地图片存储目录 | `uploads` |
| `ENV_FILE` | 否 | 指定其他环境配置文件路径 | `./tlias-web-management/.env` |

单个上传文件最大为 10 MB，multipart 请求最大为 100 MB。

## 测试

执行主后端模块的全部测试：

```bash
ENV_FILE="$PWD/tlias-web-management/.env" \
  ./tlias-web-management/mvnw -f tlias-parent/pom.xml test
```

项目包含 GitHub Actions 工作流，用于在创建 Pull Request 或向 `main` 分支推送代码时执行后端验证。
测试环境使用 H2 并关闭 Flyway；MySQL 迁移脚本需要在全新的 MySQL 数据库中单独验证。

## 阿里云 OSS Starter 示例

阿里云 OSS 相关模块是一个独立的学习示例，用于演示如何编写 Spring Boot 自动配置并封装自定义 Starter。Tlias 主应用目前仍将上传的图片保存在本地文件系统中。

运行 Starter 测试应用前，需要先安装自动配置模块和 Starter 模块。阿里云访问凭证应通过阿里云 SDK 支持的环境变量提供，例如 `OSS_ACCESS_KEY_ID` 和 `OSS_ACCESS_KEY_SECRET`，请勿将访问凭证提交到 Git。

## 后续计划

- 开发并接入 Vue 前端
- 补充单元测试与集成测试
- 补充基于 MySQL 的 Flyway 集成测试
- 完善 API 文档与部署配置
