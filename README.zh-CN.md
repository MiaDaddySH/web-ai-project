# Tlias Web Management

[English](README.md) | 简体中文

Tlias Web Management 是一个用于学习全栈 Web 应用开发的课程实践项目。课程内容包括使用 Spring Boot、MySQL、MyBatis 和 JWT 进行后端开发，以及使用 Vue 进行前端开发。

本仓库中的代码来源于课程示例与课程作业。在跟随课程学习的同时，我也会对课程实现中不够理想的部分进行重构和优化，使代码更加清晰、安全且易于维护。

> 本项目仅用于学习，目前仍在持续开发中，不应被视为可直接用于生产环境的应用。

## 当前实现

仓库目前包含后端应用，已经实现的功能包括：

- 部门信息的增删改查
- 员工信息分页查询
- 按姓名、性别和入职日期范围动态筛选员工
- Controller、Service、Mapper 分层架构
- MyBatis 动态 SQL 映射
- 统一的接口响应与分页结果模型
- 基于 Logback 的控制台日志
- Maven 测试及 GitHub Actions 持续集成

基于 Vue 的前端开发和 JWT 身份认证属于课程学习范围，将随着课程进度逐步加入项目。

## 技术栈

### 当前使用

- Java 17
- Spring Boot 4.0.8
- Spring Web MVC
- MyBatis 4.0.1
- MySQL
- PageHelper
- Lombok
- Logback
- Maven Wrapper

### 课程涉及

- JWT 身份认证
- Vue 前端开发

## 项目结构

```text
web-ai-project/
├── .github/
│   └── workflows/              # GitHub Actions 工作流
├── tlias-web-management/       # Spring Boot 后端应用
│   ├── .mvn/                   # Maven Wrapper 配置
│   ├── src/main/java/          # Java 源代码
│   │   └── com/sheng/
│   │       ├── controller/     # HTTP 接口层
│   │       ├── mapper/         # MyBatis Mapper 接口
│   │       ├── pojo/           # 领域模型及响应模型
│   │       └── service/        # 业务逻辑层
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML 映射文件
│   │   ├── application.yml     # 应用配置
│   │   └── logback.xml         # 日志配置
│   ├── src/test/java/          # 测试代码
│   ├── .env.example            # 本地配置模板
│   └── pom.xml
├── README.md                   # 英文说明（默认）
└── README.zh-CN.md             # 中文说明
```

## 本地运行

### 环境要求

- JDK 17 或更高版本
- MySQL 8.x

项目已提供 Maven Wrapper，无需单独安装 Maven。

### 配置数据库

在仓库根目录执行以下命令，创建本地环境配置文件：

```bash
cp tlias-web-management/.env.example tlias-web-management/.env
```

编辑 `tlias-web-management/.env`，填写本地数据库信息：

```properties
DB_URL=jdbc:mysql://localhost:3306/tlias
DB_USERNAME=root
DB_PASSWORD=your_password
```

`.env` 已加入 Git 忽略列表。请勿将真实密码写入 `.env.example` 或其他受 Git 管理的文件。

项目目前要求本地已经存在 `tlias` 数据库，以及课程练习所使用的数据表。

### 启动应用

请在仓库根目录执行以下命令，确保应用能够正确读取配置的 `.env` 路径：

```bash
./tlias-web-management/mvnw \
  -f tlias-web-management/pom.xml \
  spring-boot:run
```

Windows：

```powershell
tlias-web-management\mvnw.cmd `
  -f tlias-web-management\pom.xml `
  spring-boot:run
```

应用默认访问地址为 `http://localhost:8080`。

## API 概览

| 请求方法 | 地址 | 说明 |
| --- | --- | --- |
| `GET` | `/depts` | 查询全部部门 |
| `GET` | `/depts/{id}` | 根据 ID 查询部门 |
| `POST` | `/depts` | 新增部门 |
| `PUT` | `/depts` | 修改部门 |
| `DELETE` | `/depts?id={id}` | 删除部门 |
| `GET` | `/emps` | 分页查询员工并支持可选筛选条件 |

员工查询参数：

| 参数 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `page` | 否 | `1` | 页码 |
| `pageSize` | 否 | `10` | 每页记录数 |
| `name` | 否 | — | 按员工姓名模糊查询 |
| `gender` | 否 | — | 课程数据模型使用的性别值 |
| `begin` | 否 | — | 入职日期下限，格式为 `yyyy-MM-dd` |
| `end` | 否 | — | 入职日期上限，格式为 `yyyy-MM-dd` |

示例：

```text
GET /emps?page=1&pageSize=10&name=林&gender=1&begin=2024-01-01&end=2026-12-31
```

## 测试与构建

在仓库根目录执行：

```bash
./tlias-web-management/mvnw -f tlias-web-management/pom.xml test
./tlias-web-management/mvnw -f tlias-web-management/pom.xml package
```

创建 Pull Request 或向 `main` 分支推送代码时，GitHub Actions 会自动执行 Maven 验证流程。

## 配置说明

| 配置项 | 用途 | 示例 |
| --- | --- | --- |
| `DB_URL` | JDBC 连接地址 | `jdbc:mysql://localhost:3306/tlias` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | 仅保存在本地 `.env` 文件中 |

## 项目状态

本项目将跟随课程进度持续更新。随着新知识点的学习，会逐步补充业务功能、测试、文档和实现优化。
