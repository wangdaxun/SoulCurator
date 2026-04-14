# SoulCurator AI Engine

基于Python的AI数据引擎，负责：
1. 数据收集和导入管道
2. AI能力集成和调用
3. 数据整合和分析

## 项目定位
- **技术栈**: Python 3.11+
- **架构**: 微服务风格，可通过REST API集成
- **目标**: 为SoulCurator提供智能数据处理和AI能力

## 核心功能

### 1. 数据管道 (Data Pipeline)
- 从多种来源收集数据（API、文件、数据库）
- 数据清洗和转换
- 批量导入到PostgreSQL

### 2. AI能力层 (AI Capability Layer)
- 多AI模型集成（OpenAI、Claude、本地模型）
- 智能推荐算法
- 自然语言处理

### 3. 数据整合引擎 (Data Integration Engine)
- 跨数据源关联
- 数据增强和丰富
- 洞察生成

## 项目结构
```
soulcurator-ai-engine/
├── src/
│   ├── data_pipeline/      # 数据管道
│   ├── ai_core/           # AI核心
│   ├── integration/       # 数据整合
│   └── api/              # API接口
├── config/               # 配置文件
├── scripts/             # 脚本文件
├── tests/               # 测试
└── docs/                # 文档
```

## 快速开始

### 环境配置
```bash
# 1. 创建虚拟环境
python -m venv venv

# 2. 激活虚拟环境
source venv/bin/activate  # macOS/Linux
# 或
venv\Scripts\activate     # Windows

# 3. 安装依赖
pip install -r requirements.txt
```

### 配置文件
复制`.env.example`为`.env`并配置：
```env
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=soulcurator_db
DB_USER=postgres
DB_PASSWORD=your_password

# AI服务配置
OPENAI_API_KEY=your_openai_key
ANTHROPIC_API_KEY=your_claude_key

# 项目配置
LOG_LEVEL=INFO
```

## 开发指南

### 数据收集示例
```python
from src.data_pipeline.collectors.api_collector import APICollector

collector = APICollector()
data = collector.collect_from_hhxg()  # 从hhxg.top收集市场数据
```

### AI调用示例
```python
from src.ai_core.recommendation_engine import RecommendationEngine

engine = RecommendationEngine()
recommendations = engine.generate_for_portrait(portrait_id=123)
```

### API使用示例
```bash
# 启动服务
uvicorn src.api.main:app --reload

# 调用API
curl -X POST http://localhost:8000/api/v1/recommend \
  -H "Content-Type: application/json" \
  -d '{"portrait_id": 123}'
```

## 与主项目集成

### 开发模式
Python服务独立运行，直接操作数据库

### 生产模式
通过REST API与Spring Boot后端通信

## 部署

### Docker部署
```bash
docker build -t soulcurator-ai-engine .
docker run -p 8000:8000 soulcurator-ai-engine
```

### 环境变量
所有敏感配置通过环境变量管理

## 开发原则
1. **快速迭代**：有问题就问AI，立即调试
2. **实用优先**：不追求完美架构，先实现功能
3. **API驱动**：所有功能都可通过API调用
4. **错误容忍**：记录错误但不阻塞流程

## 贡献
欢迎提交Issue和Pull Request

## 许可证
MIT