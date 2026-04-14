# SoulCurator Skills

本项目相关的OpenClaw Skills集合。这些Skills可以让OpenClaw AI助手更好地理解和操作SoulCurator项目。

## 📦 可用Skills

### 1. soulcurator-manager
**功能**: SoulCurator项目管理技能
**描述**: 管理和监控SoulCurator项目状态，生成进度报告，提醒待办任务
**触发关键词**: SoulCurator项目、项目状态、进度报告、项目管理

### 2. soulcurator-html-review  
**功能**: HTML代码审查和建议工具
**描述**: 专为SoulCurator项目设计的HTML代码审查工具，提供"先建议，后行动"的工作流
**触发关键词**: HTML审查、代码审查、Vue3 HTML、前端代码

### 3. hhxg-api
**功能**: A股市场数据API访问
**描述**: 访问hhxg.top的REST API获取A股市场数据，包括市场快照、财经新闻、市场日历等
**触发关键词**: A股、股票、市场数据、财经新闻、hhxg

## 🚀 安装和使用

### 方法1：手动安装（开发环境）
```bash
# 将skill复制到OpenClaw的skills目录
cp -r soulcurator-manager ~/.openclaw/workspace/skills/

# 或者使用符号链接（推荐，便于更新）
ln -s /path/to/soulcurator/skills/soulcurator-manager ~/.openclaw/workspace/skills/
```

### 方法2：通过clawhub.ai安装（发布后）
```bash
# 发布到clawhub.ai后，其他用户可以通过以下命令安装
openclaw skills install soulcurator-manager
```

### 方法3：GitHub直接使用
```bash
# 克隆整个项目
git clone https://github.com/yourusername/soulcurator.git

# 在OpenClaw中直接引用skill路径
# 在对话中提到skill时，OpenClaw会自动加载
```

## 🔧 Skill开发指南

### Skill结构
每个Skill应包含以下文件：
```
skill-name/
├── SKILL.md              # Skill描述和使用方法（必需）
├── references/           # 参考文档（可选）
│   └── best-practices.md
├── scripts/             # 脚本文件（可选）
│   └── analyze.py
├── templates/           # 模板文件（可选）
│   └── report-template.md
└── test/               # 测试文件（可选）
    └── test-data.json
```

### SKILL.md编写规范
```markdown
# Skill名称

## 描述
清晰描述这个Skill的功能和用途

## 何时使用
- 场景1：当用户需要...
- 场景2：当用户询问...

## 使用方法
1. 第一步：...
2. 第二步：...

## 示例
用户: "帮我审查这个HTML代码"
AI: [使用这个Skill进行分析并提供建议]

## 技术依赖
- 需要安装的工具
- 需要的API密钥
- 系统要求

## 文件说明
- references/: 包含参考文档
- scripts/: 包含可执行脚本
- templates/: 包含模板文件
```

### 测试Skill
```bash
# 在OpenClaw中测试Skill
# 1. 将Skill放到正确目录
# 2. 重启OpenClaw或等待Skill缓存刷新
# 3. 使用触发关键词测试
```

## 📝 发布到clawhub.ai

### 准备发布
1. 确保Skill有完整的文档
2. 添加`package.json`或`skill.json`元数据文件
3. 编写清晰的README
4. 测试所有功能

### 发布步骤
```bash
# 1. 在clawhub.ai注册账户
# 2. 创建Skill包
# 3. 提交审核
# 4. 发布后其他用户即可安装
```

## 🤝 贡献新的Skill

欢迎为SoulCurator项目贡献新的Skill！

### 贡献流程
1. Fork本仓库
2. 在`skills/`目录下创建新的Skill
3. 遵循Skill结构规范
4. 提交Pull Request
5. 通过审核后合并

### Skill创意
- **soulcurator-deploy**: 部署和运维Skill
- **soulcurator-analytics**: 数据分析Skill  
- **soulcurator-testing**: 测试自动化Skill
- **soulcurator-documentation**: 文档生成Skill

## 📄 许可证

所有Skills采用与主项目相同的MIT许可证。

## 🔗 相关链接

- [OpenClaw Skills文档](https://docs.openclaw.ai/skills)
- [clawhub.ai Skill市场](https://clawhub.ai)
- [SoulCurator主项目](../README.md)