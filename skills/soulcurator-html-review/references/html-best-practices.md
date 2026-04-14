# HTML最佳实践 - SoulCurator项目专用

## 🎯 核心原则

### 1. 语义化HTML
```html
<!-- 好 -->
<header>
  <nav>
    <ul>
      <li><a href="/">首页</a></li>
    </ul>
  </nav>
</header>

<!-- 不好 -->
<div class="header">
  <div class="nav">
    <div class="nav-item">
      <div class="link">首页</div>
    </div>
  </div>
</div>
```

### 2. 可访问性
```html
<!-- 必须的 -->
<img src="avatar.jpg" alt="用户头像">
<button type="button" aria-label="关闭">
<input type="text" aria-describedby="help-text">

<!-- 避免 -->
<img src="avatar.jpg">
<button>关闭</button>
```

### 3. 性能优化
```html
<!-- 懒加载 -->
<img src="placeholder.jpg" data-src="real-image.jpg" loading="lazy">

<!-- 响应式图片 -->
<picture>
  <source media="(min-width: 768px)" srcset="large.jpg">
  <source media="(min-width: 480px)" srcset="medium.jpg">
  <img src="small.jpg" alt="描述">
</picture>
```

## 🎨 Vue3特定实践

### 1. 组件结构
```html
<!-- Vue3单文件组件 -->
<template>
  <!-- 语义化结构 -->
  <article class="card">
    <header class="card-header">
      <h2>{{ title }}</h2>
    </header>
    <div class="card-body">
      <slot></slot>
    </div>
    <footer class="card-footer">
      <button @click="handleClick">操作</button>
    </footer>
  </article>
</template>
```

### 2. 事件处理
```html
<!-- 明确的事件 -->
<button @click="handleSubmit" type="button">
  提交
</button>

<!-- 键盘可访问 -->
<button @keyup.enter="handleSubmit" @click="handleSubmit">
  提交
</button>
```

### 3. 条件渲染
```html
<!-- 使用v-if/v-show -->
<div v-if="isLoading">
  加载中...
</div>

<!-- 避免内联样式 -->
<div :class="{ 'is-hidden': !isVisible }">
  内容
</div>
```

## 🎯 Tailwind CSS最佳实践

### 1. 类名组织
```html
<!-- 按功能分组 -->
<div class="
  /* 布局 */
  flex items-center justify-between
  
  /* 间距 */
  p-4 space-x-4
  
  /* 颜色 */
  bg-white text-gray-800
  
  /* 响应式 */
  md:flex-col md:space-x-0 md:space-y-4
">
  内容
</div>
```

### 2. 响应式设计
```html
<!-- 移动优先 -->
<div class="
  /* 基础样式（移动端） */
  p-4 text-sm
  
  /* 平板 */
  md:p-6 md:text-base
  
  /* 桌面 */
  lg:p-8 lg:text-lg
">
  内容
</div>
```

### 3. 组件类提取
```html
<!-- 提取重复模式 -->
<div class="card">
  <div class="card-header">
    <h3 class="card-title">{{ title }}</h3>
  </div>
  <div class="card-body">
    <slot></slot>
  </div>
</div>

<style>
.card {
  @apply bg-white rounded-lg shadow-md p-6;
}
.card-header {
  @apply border-b pb-4 mb-4;
}
.card-title {
  @apply text-xl font-semibold text-gray-800;
}
.card-body {
  @apply text-gray-600;
}
</style>
```

## 🔧 代码审查清单

### 必须检查项
- [ ] 所有图片都有alt属性
- [ ] 所有按钮都有type属性
- [ ] 表单元素有label或aria-label
- [ ] 使用语义化标签（header, nav, main, section, article, footer）
- [ ] 避免使用<br>进行布局
- [ ] 避免使用<table>进行非表格布局

### 建议检查项
- [ ] 响应式设计（移动端友好）
- [ ] 颜色对比度（WCAG AA标准）
- [ ] 键盘导航支持
- [ ] 图片懒加载
- [ ] 字体大小可调整

### Vue3特定检查
- [ ] 使用Composition API（setup语法）
- [ ] Props有类型定义
- [ ] 事件处理明确
- [ ] 避免在模板中使用复杂逻辑
- [ ] 组件有明确的name属性

## 📊 性能指标

### 1. 加载性能
- 首屏加载时间 < 3秒
- 关键资源预加载
- 图片优化（WebP格式）

### 2. 渲染性能
- 避免强制同步布局
- 使用CSS动画代替JS动画
- 虚拟滚动长列表

### 3. 内存使用
- 及时清理事件监听器
- 避免内存泄漏
- 使用WeakMap/WeakSet

## 🚀 优化建议

### 1. 结构优化
```html
<!-- 扁平化DOM -->
<div class="user-profile">
  <img :src="avatar" :alt="name + '的头像'">
  <h3>{{ name }}</h3>
  <p>{{ bio }}</p>
</div>

<!-- 避免 -->
<div>
  <div>
    <div>
      <div>...</div>
    </div>
  </div>
</div>
```

### 2. 可维护性
```html
<!-- 使用有意义的类名 -->
<div class="user-card" data-user-id="123">
  <!-- 内容 -->
</div>

<!-- 避免 -->
<div class="div1">
  <!-- 内容 -->
</div>
```

### 3. 国际化考虑
```html
<!-- 支持RTL -->
<div dir="auto" class="text-start">
  内容
</div>

<!-- 语言属性 -->
<html lang="zh-CN">
```

## 📝 常见问题及解决方案

### 问题1：图片加载慢
**解决方案**:
```html
<img 
  :src="imageUrl" 
  :alt="imageAlt"
  loading="lazy"
  decoding="async"
>
```

### 问题2：按钮点击无反馈
**解决方案**:
```html
<button 
  @click="handleClick"
  type="button"
  :disabled="isLoading"
  :class="{ 'opacity-50 cursor-not-allowed': isLoading }"
>
  <span v-if="isLoading">加载中...</span>
  <span v-else>点击我</span>
</button>
```

### 问题3：表单验证
**解决方案**:
```html
<form @submit.prevent="handleSubmit">
  <label for="email">邮箱</label>
  <input 
    id="email"
    type="email"
    v-model="email"
    required
    aria-describedby="email-help"
  >
  <div id="email-help" class="text-sm text-gray-500">
    请输入有效的邮箱地址
  </div>
  
  <button type="submit" :disabled="!isValid">
    提交
  </button>
</form>
```

---

**最后更新**: 2026-04-03  
**适用于**: SoulCurator项目 (Vue3 + TypeScript + Tailwind CSS)