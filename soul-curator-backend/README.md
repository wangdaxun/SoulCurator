# SoulCurator Backend

灵魂策展人 - 后端服务

## 项目简介

SoulCurator是一个基于个人文艺兴趣的智能推荐系统，帮助用户发现情感深度相似的文艺作品（游戏、电影、书籍等）。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.5**
- **PostgreSQL 15** (主数据库)
- **Redis 7** (缓存)
- **Spring Security + JWT** (安全认证)
- **Spring Data JPA** (数据访问)
- **Docker + Docker Compose** (容器化)

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.9+
- Docker & Docker Compose (可选)
- PostgreSQL 15 (可选，开发环境)
- Redis 7 (可选，开发环境)

### 2. 本地开发

#### 使用Docker Compose（推荐）

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f backend

# 停止服务
docker-compose down
```

#### 手动启动

1. 启动PostgreSQL和Redis
2. 配置数据库连接（修改`application-dev.yml`）
3. 运行应用：

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. API文档

应用启动后，访问以下地址：

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API文档: http://localhost:8080/api/v3/api-docs
- 健康检查: http://localhost:8080/api/actuator/health

## 项目结构

```
src/main/java/com/soulcurator/backend/
├── config/              # 配置类
├── controller/          # 控制器层
│   └── api/v1/         # API版本1
├── service/            # 服务层
│   ├── interface/      # 服务接口
│   └── impl/          # 服务实现
├── repository/         # 数据访问层
├── model/              # 数据模型
│   ├── entity/        # 数据库实体
│   ├── dto/           # 数据传输对象
│   └── enums/         # 枚举类
├── security/           # 安全相关
├── exception/          # 异常处理
└── util/              # 工具类
```

## 核心功能

### 1. 作品管理
- 游戏、电影、书籍等文艺作品的CRUD
- 作品标签管理（情感、主题、风格）
- 作品搜索和筛选
- 外部数据同步（豆瓣、Steam等）

### 2. 用户系统
- 用户注册登录（JWT认证）
- 用户偏好管理
- 作品收藏和评分
- 用户画像分析

### 3. 智能推荐
- 基于情感的匹配推荐
- 基于主题的探索推荐
- 个性化成长路径
- 反信息茧房算法

### 4. 分析系统
- 作品深度分析（情感、主题、风格）
- 用户行为分析
- 推荐效果评估
- AI增强分析

## 开发指南

### 数据库迁移

项目使用JPA的`ddl-auto: update`模式，在开发环境会自动更新表结构。

生产环境建议：
1. 设置`ddl-auto: validate`
2. 使用Flyway或Liquibase进行数据库迁移

### 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=WorkServiceTest

# 生成测试报告
mvn surefire-report:report
```

### 代码规范

1. 使用Lombok减少样板代码
2. 遵循RESTful API设计原则
3. 统一异常处理
4. 使用DTO进行数据传输
5. 添加Swagger注解

## 部署

### 构建Docker镜像

```bash
# 构建镜像
docker build -t soulcurator-backend:latest .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/soulcurator \
  --name soulcurator-backend \
  soulcurator-backend:latest
```

### 生产环境配置

1. 修改`application-prod.yml`中的环境变量
2. 配置数据库连接池
3. 设置JWT密钥
4. 配置日志和监控

## 监控和运维

### 健康检查端点

- `/api/actuator/health` - 应用健康状态
- `/api/actuator/info` - 应用信息
- `/api/actuator/metrics` - 应用指标
- `/api/actuator/env` - 环境变量

### 日志

日志文件位于`logs/soul-curator.log`，按天滚动，最大保留30天。

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

- 项目主页: https://github.com/yourusername/soul-curator
- 问题反馈: https://github.com/yourusername/soul-curator/issues

---

**SoulCurator - 不是推荐你看什么，而是帮你发现你想成为谁。**