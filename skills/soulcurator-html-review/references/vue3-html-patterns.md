# Vue3 HTML模式 - SoulCurator项目专用

## 🎯 Vue3组件设计模式

### 1. 组合式API模式
```html
<template>
  <!-- 模板保持简洁 -->
  <div class="component">
    <h2>{{ title }}</h2>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
// 逻辑在setup中
import { ref, computed } from 'vue'

const props = defineProps<{
  title: string
  initialCount?: number
}>()

const count = ref(props.initialCount || 0)
const doubled = computed(() => count.value * 2)

const increment = () => {
  count.value++
}
</script>
```

### 2. 组件通信模式
```html
<!-- 父组件 -->
<template>
  <ChildComponent 
    :message="parentMessage"
    @update="handleUpdate"
  />
</template>

<!-- 子组件 -->
<template>
  <div>
    <p>{{ message }}</p>
    <button @click="$emit('update', newValue)">
      更新
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message: string
}>()

defineEmits<{
  update: [value: string]
}>()
</script>
```

## 🎨 Tailwind + Vue3集成模式

### 1. 响应式组件
```html
<template>
  <div class="
    /* 基础样式 */
    p-4 rounded-lg shadow
    
    /* 响应式 */
    sm:p-6
    md:flex md:items-center md:space-x-4
    lg:p-8
    
    /* 状态 */
    hover:shadow-lg
    transition-shadow duration-200
  ">
    <div class="flex-shrink-0">
      <img :src="avatar" :alt="name" class="w-12 h-12 rounded-full">
    </div>
    <div class="mt-4 md:mt-0">
      <h3 class="text-lg font-semibold">{{ name }}</h3>
      <p class="text-gray-600">{{ bio }}</p>
    </div>
  </div>
</template>
```

### 2. 动态类名
```html
<template>
  <button
    :class="[
      'px-4 py-2 rounded font-medium',
      'transition-colors duration-200',
      variantClasses,
      sizeClasses,
      { 'opacity-50 cursor-not-allowed': disabled }
    ]"
    :disabled="disabled"
  >
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
const props = defineProps<{
  variant?: 'primary' | 'secondary' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
}>()

const variantClasses = computed(() => {
  switch (props.variant) {
    case 'primary':
      return 'bg-blue-600 text-white hover:bg-blue-700'
    case 'secondary':
      return 'bg-gray-200 text-gray-800 hover:bg-gray-300'
    case 'danger':
      return 'bg-red-600 text-white hover:bg-red-700'
    default:
      return 'bg-blue-600 text-white hover:bg-blue-700'
  }
})

const sizeClasses = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'text-sm px-3 py-1'
    case 'lg':
      return 'text-lg px-6 py-3'
    default:
      return 'text-base px-4 py-2'
  }
})
</script>
```

## 🔧 常用组件模式

### 1. 卡片组件
```html
<template>
  <article class="card" :class="cardClasses">
    <header v-if="$slots.header || title" class="card-header">
      <slot name="header">
        <h3 class="card-title">{{ title }}</h3>
      </slot>
    </header>
    
    <div class="card-body">
      <slot></slot>
    </div>
    
    <footer v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </footer>
  </article>
</template>

<style scoped>
.card {
  @apply bg-white rounded-lg shadow-md overflow-hidden;
}
.card-header {
  @apply px-6 py-4 border-b border-gray-200;
}
.card-title {
  @apply text-xl font-semibold text-gray-800;
}
.card-body {
  @apply p-6;
}
.card-footer {
  @apply px-6 py-4 border-t border-gray-200 bg-gray-50;
}
</style>
```

### 2. 表单组件
```html
<template>
  <div class="form-group">
    <label :for="id" class="form-label">
      {{ label }}
      <span v-if="required" class="text-red-500">*</span>
    </label>
    
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="placeholder"
      :required="required"
      :class="[
        'form-input',
        { 'border-red-500': error }
      ]"
      :aria-describedby="error ? `${id}-error` : null"
    >
    
    <div v-if="error" :id="`${id}-error`" class="form-error">
      {{ error }}
    </div>
    
    <div v-if="helpText" class="form-help">
      {{ helpText }}
    </div>
  </div>
</template>

<style scoped>
.form-group {
  @apply mb-4;
}
.form-label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}
.form-input {
  @apply block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm 
         focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500;
}
.form-error {
  @apply mt-1 text-sm text-red-600;
}
.form-help {
  @apply mt-1 text-sm text-gray-500;
}
</style>
```

## 🚀 性能优化模式

### 1. 列表虚拟滚动
```html
<template>
  <div ref="container" class="virtual-list" @scroll="handleScroll">
    <div :style="{ height: `${totalHeight}px` }">
      <div
        v-for="item in visibleItems"
        :key="item.id"
        :style="{ transform: `translateY(${item.offset}px)` }"
        class="absolute left-0 right-0"
      >
        <!-- 列表项内容 -->
        {{ item.content }}
      </div>
    </div>
  </div>
</template>
```

### 2. 图片懒加载
```html
<template>
  <img
    :src="placeholder"
    :data-src="src"
    :alt="alt"
    class="lazy-image"
    @load="handleLoad"
  />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

const props = defineProps<{
  src: string
  alt: string
  placeholder?: string
}>()

const imgRef = ref<HTMLImageElement>()

const handleLoad = () => {
  // 图片加载完成后的处理
}

onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const img = entry.target as HTMLImageElement
        img.src = img.dataset.src || ''
        observer.unobserve(img)
      }
    })
  })
  
  if (imgRef.value) {
    observer.observe(imgRef.value)
  }
})
</script>
```

## 📊 状态管理模式

### 1. 本地状态
```html
<template>
  <div>
    <p>计数: {{ count }}</p>
    <button @click="increment">增加</button>
    <button @click="reset">重置</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const count = ref(0)

const increment = () => {
  count.value++
}

const reset = () => {
  count.value = 0
}
</script>
```

### 2. 全局状态（Pinia）
```html
<template>
  <div>
    <p>用户: {{ userStore.name }}</p>
    <button @click="userStore.login()">登录</button>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>
```

## 🎨 样式模式

### 1. Scoped样式
```html
<template>
  <div class="custom-component">
    <!-- 内容 -->
  </div>
</template>

<style scoped>
/* 只影响当前组件 */
.custom-component {
  color: red;
}

/* 深度选择器 */
.custom-component :deep(.child) {
  color: blue;
}

/* 插槽内容 */
.custom-component ::v-slotted(.slot-content) {
  color: green;
}
</style>
```

### 2. CSS Modules
```html
<template>
  <div :class="$style.container">
    <h1 :class="$style.title">标题</h1>
  </div>
</template>

<style module>
.container {
  padding: 20px;
}

.title {
  font-size: 24px;
  color: #333;
}
</style>
```

## 🔧 工具函数模式

### 1. 组合函数
```html
<template>
  <div>
    <p>鼠标位置: {{ x }}, {{ y }}</p>
    <p>点击次数: {{ clickCount }}</p>
  </div>
</template>

<script setup lang="ts">
import { useMouse, useCounter } from '@/composables'

const { x, y } = useMouse()
const { count: clickCount, increment } = useCounter()
</script>
```

### 2. 工具组件
```html
<template>
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <LoadingSpinner />
    </template>
  </Suspense>
</template>
```

## 📝 最佳实践总结

### 必须遵守
1. **使用Composition API**（setup语法）
2. **TypeScript类型定义**
3. **语义化HTML标签**
4. **可访问性属性**
5. **响应式设计**

### 建议遵守
1. **组件单一职责**
2. **Props验证**
3. **事件命名规范**
4. **样式作用域**
5. **性能优化**

### 避免
1. **避免在模板中使用复杂逻辑**
2. **避免直接操作DOM**
3. **避免全局样式污染**
4. **避免过深的组件嵌套**
5. **避免不必要的重新渲染**

---

**最后更新**: 2026-04-03  
**适用于**: SoulCurator项目 Vue3组件开发