# SoulCurator 第二阶段：问题卡片组件实现提示词

## 项目背景
- **项目名称**: SoulCurator（灵魂策展人）
- **当前阶段**: 第二阶段 - 问题流程实现
- **选择策略**: 选择B - 先创建问题卡片组件
- **技术栈**: Vue3 + TypeScript + Vite + Tailwind CSS
- **项目目录**: `/Users/wangdaxun/SoulCurator/soul-curator-frontend`

## 第一阶段成果回顾
✅ **已完成**:
1. 入口页面 (`EntryGuide.vue`) - 艺术形式选择
2. 灵魂之门组件 (`SoulGate.vue`) - 带碎裂动画的选择门
3. 粒子背景组件 (`ParticleBackground.vue`)
4. 5个静态问题设计文档 (`soul-curator-movie-questions.md`)

## 第二阶段目标：问题卡片组件实现

### 核心组件需求
1. **QuestionCard.vue** - 主问题卡片组件
2. **AnswerOption.vue** - 单个答案选项组件  
3. **MovieExample.vue** - 电影例子展示组件
4. **QuestionFlow.vue** - 问题流程容器组件

### 设计原则
1. **视觉一致性**: 延续入口页面的暗黑文艺风格
2. **交互流畅性**: 平滑的过渡动画和状态反馈
3. **信息清晰性**: 问题、选项、例子分层展示
4. **响应式设计**: 适配不同屏幕尺寸

## 详细实现提示词

### 提示词1：QuestionCard.vue 主组件实现

```
请创建一个 Vue3 单文件组件 QuestionCard.vue，用于展示 SoulCurator 的电影偏好分析问题。

组件要求：

1. **视觉风格**：
   - 延续入口页面的暗黑文艺风格（背景色：#050507）
   - 使用与 SoulGate 组件一致的设计语言
   - 添加微妙的发光效果和模糊背景

2. **组件结构**：
   - 顶部：问题标题和序号（如"问题1/5"）
   - 中部：问题描述（大字体，优雅排版）
   - 下部：4个答案选项（使用 AnswerOption 子组件）
   - 底部：导航按钮（上一题/下一题）

3. **交互功能**：
   - 点击选项时高亮显示
   - 选择后显示确认状态
   - 平滑的过渡动画
   - 支持键盘导航（方向键选择，回车确认）

4. **Props 设计**：
   - question: Object - 包含问题数据
   - currentIndex: Number - 当前问题索引
   - totalQuestions: Number - 总问题数
   - selectedAnswer: String|null - 当前选择的答案

5. **事件发射**：
   - @answer-selected: 用户选择答案时触发
   - @prev-question: 点击上一题时触发
   - @next-question: 点击下一题时触发

6. **样式要求**：
   - 使用 Tailwind CSS 类名
   - 添加自定义 CSS 变量用于主题色
   - 实现响应式布局
   - 添加微交互动画（hover、active状态）

请参考现有 SoulGate 组件的设计风格和代码结构。
```

### 提示词2：AnswerOption.vue 选项组件实现

```
请创建一个 Vue3 单文件组件 AnswerOption.vue，用于展示单个答案选项。

组件要求：

1. **视觉设计**：
   - 卡片式设计，有边框和阴影
   - 未选中状态：半透明，轻微发光
   - 选中状态：高亮，边框发光，背景变亮
   - hover状态：轻微放大，发光增强

2. **内容结构**：
   - 顶部：选项标题（加粗）
   - 中部：特点描述（简短）
   - 下部：电影例子列表（使用 MovieExample 子组件）
   - 右侧：选择状态指示器（选中时显示✓）

3. **交互功能**：
   - 点击选择/取消选择
   - 键盘可访问性
   - 触摸设备友好

4. **Props 设计**：
   - option: Object - 选项数据
   - index: Number - 选项索引（0-3）
   - isSelected: Boolean - 是否被选中
   - isCorrect: Boolean - 是否正确（预留）

5. **样式细节**：
   - 使用与 SoulGate 一致的颜色主题
   - 添加破碎玻璃效果的选中动画
   - 实现平滑的状态过渡
```

### 提示词3：MovieExample.vue 电影例子组件

```
请创建一个 Vue3 单文件组件 MovieExample.vue，用于展示电影例子。

组件要求：

1. **视觉设计**：
   - 简约的标签式设计
   - 电影名称 + 简短描述
   - 添加电影海报风格的背景色
   - 微妙的胶片边框效果

2. **内容展示**：
   - 电影名称（中文+英文）
   - 关键场景描述
   - 情感关键词标签
   - 导演和年份（小字）

3. **交互功能**：
   - hover时显示更多信息
   - 点击可展开详细分析（预留）

4. **Props 设计**：
   - movie: Object - 电影数据
   - compact: Boolean - 紧凑模式（默认false）

5. **样式特点**：
   - 每个电影类型有对应的颜色主题
   - 添加胶片颗粒纹理效果
   - 实现淡入淡出动画
```

### 提示词4：QuestionFlow.vue 流程容器组件

```
请创建一个 Vue3 单文件组件 QuestionFlow.vue，作为问题流程的容器。

组件要求：

1. **页面结构**：
   - 顶部：ProgressBar 进度条组件（预留）
   - 中部：QuestionCard 问题卡片
   - 底部：全局导航和控制按钮

2. **状态管理**：
   - 使用 Pinia 存储用户答案
   - 管理当前问题索引
   - 处理问题切换逻辑
   - 保存进度到本地存储

3. **路由集成**：
   - 与 Vue Router 集成
   - 支持直接访问特定问题
   - 实现页面过渡动画

4. **功能特性**：
   - 断点续传：刷新页面后恢复进度
   - 答案修改：可以返回修改之前的答案
   - 进度保存：自动保存到 localStorage
   - 数据验证：确保所有问题已回答

5. **Props & 事件**：
   - 接收问题数据数组
   - 发射完成事件（所有问题已回答）
   - 提供重置功能
```

### 提示词5：问题数据结构和状态管理

```
请设计 SoulCurator 问题流程的数据结构和状态管理方案。

要求：

1. **问题数据结构**：
```typescript
interface Question {
  id: string
  index: number
  title: string
  description: string
  options: AnswerOption[]
}

interface AnswerOption {
  id: string
  title: string
  description: string
  examples: MovieExample[]
  characteristics: string[]
}

interface MovieExample {
  id: string
  chineseTitle: string
  englishTitle: string
  director: string
  year: number
  keyScene: string
  emotionTags: string[]
  colorTheme: string
}
```

2. **Pinia Store 设计**：
```typescript
// store/question.ts
export const useQuestionStore = defineStore('question', {
  state: () => ({
    // 当前状态
    currentQuestionIndex: 0,
    answers: {} as Record<string, string>,
    
    // 问题数据
    questions: [] as Question[],
    
    // 进度信息
    progress: 0,
    startedAt: null as Date|null,
    completedAt: null as Date|null
  }),
  
  actions: {
    // 选择答案
    selectAnswer(questionId: string, optionId: string) {},
    
    // 导航到下一题
    nextQuestion() {},
    
    // 导航到上一题
    prevQuestion() {},
    
    // 计算进度
    calculateProgress() {},
    
    // 保存到本地存储
    saveToLocalStorage() {},
    
    // 从本地存储恢复
    restoreFromLocalStorage() {},
    
    // 提交所有答案
    submitAnswers() {}
  },
  
  getters: {
    // 当前问题
    currentQuestion: (state) => {},
    
    // 是否完成
    isCompleted: (state) => {},
    
    // 已回答问题数
    answeredCount: (state) => {},
    
    // 可以提交
    canSubmit: (state) => {}
  }
})
```

3. **本地存储策略**：
   - 使用 localStorage 保存进度
   - 加密敏感数据（可选）
   - 设置过期时间（7天）
   - 冲突解决机制

4. **API 集成预留**：
   - 从服务器加载问题数据
   - 提交答案到后端
   - 获取AI分析结果
```

## 实现步骤建议

### 步骤1：创建基础组件结构
1. 在 `src/components/` 下创建组件文件
2. 实现基本的模板和样式
3. 添加必要的 TypeScript 类型定义

### 步骤2：实现交互逻辑
1. 完成组件间的数据传递
2. 实现状态管理和响应式
3. 添加动画和过渡效果

### 步骤3：集成到主应用
1. 创建问题流程路由页面
2. 配置 Pinia store
3. 设置页面过渡动画

### 步骤4：测试和优化
1. 测试不同屏幕尺寸
2. 优化性能（懒加载、代码分割）
3. 添加错误边界和加载状态

## 视觉设计参考

### 颜色主题（延续入口页面）
- 主背景：`#050507`
- 文字主色：`rgba(255, 255, 255, 0.9)`
- 文字副色：`rgba(255, 255, 255, 0.4)`
- 高亮色：根据问题类型变化
- 发光效果：`rgba(147, 51, 234, 0.4)` 等

### 动画效果
1. **问题切换**：淡入淡出 + 轻微缩放
2. **选项选择**：破碎玻璃效果 + 发光脉冲
3. **进度更新**：数字滚动 + 进度条填充
4. **状态反馈**：微交互提示

### 排版规范
- 标题字体：Serif 字体，`tracking-widest`
- 正文字体：Sans-serif 字体，`leading-relaxed`
- 例子字体：Monospace 字体，较小字号
- 间距系统：8px 基准，倍数关系

## 技术注意事项

### Vue3 特性使用
- 使用 `<script setup>` 语法
- 使用 Composition API
- 使用 `defineProps` 和 `defineEmits`
- 使用 `ref` 和 `computed`

### 性能优化
- 组件懒加载
- 图片懒加载
- 虚拟滚动（如果问题很多）
- 防抖和节流

### 可访问性
- 键盘导航支持
- ARIA 标签
- 颜色对比度检查
- 屏幕阅读器兼容

## 下一步计划

### 完成本阶段后
1. 实现 ProgressBar 进度条组件
2. 创建用户画像展示页面
3. 集成AI提示词接口
4. 实现电影推荐结果展示

### 长期规划
1. 扩展其他艺术形式（文学、音乐、游戏）
2. 实现AI动态问题生成
3. 添加社交分享功能
4. 创建用户个人资料库

---
**生成时间**：2026-04-01 23:50
**生成者**：鞠大毛_mac版
**文档位置**：`/Users/wangdaxun/SoulCurator/documents/phase2-question-card-implementation.md`
**关联文件**：`soul-curator-movie-questions.md`
**状态**：第二阶段实现提示词生成完成，准备开始编码