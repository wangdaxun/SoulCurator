---
name: soulcurator-html-review
description: "HTML代码审查和建议工具，专为SoulCurator项目设计。提供'先建议，后行动'的工作流：分析HTML代码，给出多个修改方案，等待用户选择后再执行修改。避免AI直接莽撞修改项目代码。"
metadata:
  {
    "openclaw":
      {
        "emoji": "🔍",
        "requires": { "bins": ["node"] },
        "triggers": ["html", "前端代码", "ui审查", "代码建议", "vue组件", "tailwind", "先建议"]
      },
  }
---

# SoulCurator HTML代码审查工具

专为SoulCurator项目设计的"先建议，后行动"HTML代码审查工具。当你提供HTML代码时，我会先分析并给出多个修改方案，等你选择后再执行修改。

## 🎯 核心原则

**"先建议，后行动"** - 绝不直接修改你的代码，除非你明确选择方案。

## 🔧 技术上下文

**项目技术栈**:
- **框架**: Vue3 + TypeScript + Vite
- **样式**: Tailwind CSS
- **后端**: Spring Boot + PostgreSQL
- **项目路径**: `/Users/wangdaxun/SoulCurator/soul-curator-frontend`

## 📋 使用流程

### 1. 提供HTML代码
```html
<!-- 你的HTML代码 -->
<div class="container">
  <h1>标题</h1>
  <p>内容...</p>
</div>
```

### 2. 我分析并给出建议
我会提供：
- **当前问题分析**
- **3个修改方案**（保守/优化/激进）
- **每个方案的优缺点**

### 3. 你选择方案
告诉我选择哪个方案（A/B/C）或提出具体要求。

### 4. 我执行修改
根据你的选择，执行相应的修改。

## 🎨 分析维度

### 1. 语义结构
- HTML5语义标签使用
- 可访问性（ARIA属性）
- 层级结构合理性

### 2. Vue3兼容性
- 组件化建议
- Composition API适配
- Props/Events设计

### 3. Tailwind CSS优化
- 类名优化
- 响应式设计
- 颜色/间距系统

### 4. 性能考虑
- 图片优化
- 懒加载
- 代码分割

## 📁 方案模板

### 方案A（保守）
- 最小改动
- 保持现有结构
- 快速修复关键问题

### 方案B（优化）
- 适度重构
- 应用最佳实践
- 提升代码质量

### 方案C（激进）
- 完全重写
- 最新技术栈
- 最佳性能

## 🚀 使用示例

**用户输入**:
```
帮我审查这段HTML代码，想改成Vue3组件：

<div class="user-profile">
  <img src="avatar.jpg">
  <h3>王达迅</h3>
  <p>前端开发者 | INTP | 喜欢《三体》</p>
  <button>关注</button>
</div>
```

**我的响应**:
```
## 🔍 HTML代码审查报告

### 📊 当前分析
1. ✅ 结构清晰
2. ⚠️ 缺少alt属性（可访问性问题）
3. ⚠️ 硬编码内容（不利于复用）
4. ⚠️ 按钮缺少类型属性

### 🎯 建议方案

**方案A（保守）**:
- 添加alt="用户头像"
- 添加button type="button"
- 保持现有结构
- 预计耗时：5分钟

**方案B（优化）**:
- 重构为Vue3函数组件
- 添加props: avatar, name, bio, isFollowing
- 使用Tailwind类名
- 添加点击事件处理
- 预计耗时：15分钟

**方案C（激进）**:
- 完全重写为TypeScript组件
- 添加Composition API
- 实现响应式设计
- 添加单元测试
- 预计耗时：30分钟

请选择方案（A/B/C）或告诉我你的具体要求。
```

## 📝 命令参考

### 1. 基础审查
```bash
# 分析HTML文件
cat /path/to/file.html | grep -n "<div\|<span\|<button"

# 检查Tailwind类名使用
grep -o "class=\"[^\"]*\"" file.html | tr ' ' '\n' | sort | uniq -c
```

### 2. Vue3组件生成
```bash
# 生成Vue3组件模板
cat > UserProfile.vue << 'EOF'
<template>
  <!-- 根据方案生成 -->
</template>

<script setup lang="ts">
// TypeScript代码
</script>

<style scoped>
/* 样式代码 */
</style>
EOF
```

### 3. 代码质量检查
```bash
# 检查可访问性
grep -n "alt=\|aria-" file.html

# 检查响应式设计
grep -n "sm:\|md:\|lg:\|xl:" file.html
```

## 🔗 相关文件

### 项目文件
- `references/html-best-practices.md` - HTML最佳实践
- `references/vue3-html-patterns.md` - Vue3 HTML模式
- `scripts/analyze-html.py` - HTML分析脚本
- `templates/review-template.md` - 审查报告模板

### SoulCurator项目
- 前端项目: `/Users/wangdaxun/SoulCurator/soul-curator-frontend`
- 项目文档: `/Users/wangdaxun/SoulCurator/documents`

## 💡 最佳实践

1. **始终先给建议** - 绝不直接修改代码
2. **提供多个选项** - 让用户有选择权
3. **考虑项目上下文** - 基于SoulCurator技术栈
4. **保持沟通** - 明确每个步骤的意图

## 🚨 注意事项

- 这个skill只提供建议，不自动执行修改
- 所有修改都需要用户明确确认
- 保持与现有项目结构的一致性
- 尊重用户的编码风格偏好

---

**触发关键词**: html审查, 前端代码建议, vue组件优化, tailwind审查, 先建议后修改