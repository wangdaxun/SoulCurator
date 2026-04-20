# SoulCurator (灵魂策展人)

> 不是推荐你看什么，而是帮你发现**你想成为谁**

## 🎯 项目愿景

SoulCurator 是一个基于AI的灵魂探索和个性化推荐平台。通过游戏化的情感探索流程，帮助用户发现自己的情感维度，生成独特的灵魂画像，并基于此提供个性化的文艺作品推荐。

## ✨ 核心价值

1. **产品有灵魂**: 情感连接、成长轨迹、审美教育、自我发现
2. **填补真实空白**: 技术深度 + 文艺深度
3. **对抗算法茧房**: 推荐成长路径，注重情感共鸣
4. **证明"慢产品"价值**: 在快时代追求深度
6. **社会价值**: 审美普惠、情感教育、思考训练

## 🏗️ 技术架构

### 前端技术栈
- **框架**: Vue3 + JavaScript + Vite
- **状态管理**: Pinia
- **UI框架**: Tailwind CSS + Element Plus
- **路由**: Vue Router 4
- **构建工具**: Vite 5

### 后端技术栈
- **框架**: Spring Boot 3.2.5 + Java 17
- **数据库**: PostgreSQL 15+
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **API文档**: SpringDoc OpenAPI 3

### AI引擎技术栈
- **语言**: Python 3.11+
- **Web框架**: FastAPI
- **AI集成**: OpenAI API + Claude API + LangChain
- **数据处理**: Pandas + NumPy
- **数据库**: SQLAlchemy + Alembic

### 部署架构
- **容器化**: Docker + Docker Compose
- **编排**: Kubernetes (可选)
- **监控**: Prometheus + Grafana
- **日志**: ELK Stack

## 📁 项目结构

```
soulcurator/
├── frontend/                 # Vue3前端项目
│   ├── src/
│   │   ├── components/      # Vue组件
│   │   ├── views/          # 页面视图
│   │   ├── stores/         # Pinia状态管理
│   │   ├── router/         # 路由配置
│   │   └── assets/         # 静态资源
│   └── package.json
├── backend/                  # Spring Boot后端项目
│   ├── src/main/java/com/soulcurator/backend/
│   │   ├── controller/     # API控制器
│   │   ├── service/        # 业务逻辑层
│   │   ├── repository/     # 数据访问层
│   │   ├── model/          # 实体类
│   │   ├── dto/            # 数据传输对象
│   │   └── config/         # 配置类
│   └── pom.xml
├── ai-engine/                # Python AI引擎
│   ├── src/
│   │   ├── data_pipeline/  # 数据管道
│   │   ├── ai_core/        # AI核心能力
│   │   ├── integration/    # 数据整合
│   │   └── api/            # FastAPI接口
│   └── requirements.txt
├── skills/                   # OpenClaw Skills
│   ├── soulcurator-manager/          # 项目管理技能
│   ├── soulcurator-html-review/      # HTML代码审查
│   └── hhxg-api/                     # 财经数据API
├── documents/                # 项目文档
│   ├── database-schema.md    # 数据库设计
│   ├── api-documentation.md  # API文档
│   ├── architecture.md       # 架构设计
│   └── user-guide.md         # 用户指南
├── scripts/                  # 部署和工具脚本
├── docker-compose.yml        # Docker编排配置
└── README.md                 # 本文件
```

## 🚀 快速开始

### 环境要求
- Docker 20.10+
- Docker Compose 2.20+
- Node.js 18+ (前端开发)
- Java 17+ (后端开发)
- Python 3.11+ (AI引擎开发)

### 一键启动开发环境

```bash
# 克隆项目
git clone https://github.com/yourusername/soulcurator.git
cd soulcurator

# 启动所有服务
docker-compose up -d

# 访问服务
# 前端: http://localhost:5173
# 后端API: http://localhost:8080
# API文档: http://localhost:8080/swagger-ui.html
# AI引擎API: http://localhost:8000/docs
```

### 分服务启动
```bash
# 启动数据库和缓存
docker-compose up postgres redis -d

# 开发前端
cd soul-curator-frontend
npm install
npm run dev

# 开发后端
cd soul-curator-backend
./mvnw spring-boot:run

# 开发AI引擎
cd ai-engine
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn src.api.main:app --reload
```

## 📊 数据库设计

项目使用11个核心表支持完整的灵魂探索流程：

1. **users** - 用户信息
2. **questions** - 探索问题
3. **options** - 问题选项
4. **soul_dimensions** - 灵魂维度定义
5. **user_selections** - 用户选择记录
6. **soul_portraits** - 灵魂画像
7. **personalized_recommendations** - 个性化推荐
8. **recommended_works** - 推荐作品库
9. **simple_works** - 简单作品信息
10. **user_sessions** - 用户会话
11. **user_events** - 用户行为事件

详细数据库设计见 [documents/database-schema.md](documents/database-schema.md)

## 🔧 开发指南

### 代码规范
- **前端**: ESLint + Prettier + TypeScript严格模式
- **后端**: Checkstyle + SpotBugs + Google Java Style Guide
- **Python**: Black + isort + flake8
- **提交规范**: Conventional Commits
- **分支策略**: Git Flow

### API设计规范
- RESTful风格设计
- 统一响应格式
- 版本控制 (`/api/v1/...`)
- OpenAPI 3.0文档

### 测试策略
- **单元测试**: 覆盖核心业务逻辑
- **集成测试**: 测试服务间集成
- **端到端测试**: 测试完整用户流程
- **性能测试**: 确保系统性能指标

## 🤝 贡献指南

欢迎提交Issue和Pull Request！请确保：

1. 遵循项目的代码规范
2. 添加相应的测试用例
3. 更新相关文档
4. 使用描述性的提交信息

### 开发流程
1. Fork本仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- 感谢所有开源项目的贡献者
- 感谢AI技术的发展让个人开发者能够实现复杂项目
- 特别感谢OpenClaw项目提供的AI助手基础设施

## 📞 联系方式

- **项目主页**: https://github.com/wangdaxun/SoulCurator
- **问题反馈**: https://github.com/yourusername/SoulCurator/issues
- **邮箱**: 835321694@qq.com

---

**让每一次文艺消费都成为一次自我发现的旅程**