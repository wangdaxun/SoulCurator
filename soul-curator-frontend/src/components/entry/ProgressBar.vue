<template>
  <!-- 进度条容器 -->
  <header class="w-full pt-12 pb-8 px-10 relative">
    <div class="max-w-[1200px] mx-auto flex justify-between items-start">
      
      <!-- 动态生成所有步骤 -->
      <div 
        v-for="step in steps" 
        :key="step.id"
        class="step-container"
        :class="{ 'current-step': step.id === currentStep }"
      >
        <!-- 步骤内容 -->
        <div class="step-content">
          <!-- 图标 -->
          <div class="step-icon" :class="getStepIconClass(step)">
            <Icon :name="step.icon" :size="16" />
          </div>
          
          <!-- 标签 -->
          <span class="step-label" :class="getStepLabelClass(step)">
            {{ step.label }}
          </span>
        </div>
        
        <!-- 连接线（除了最后一个步骤） -->
        <div 
          v-if="step.id < totalSteps"
          class="connector"
          :class="getConnectorClass(step)"
        ></div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { defineProps } from 'vue'
import Icon from '@/components/shared/Icon.vue'

const props = defineProps({
  currentStep: {
    type: Number,
    default: 2, // 默认在第2步：情感共鸣
    validator: (value) => value >= 1 && value <= 7
  },
  totalSteps: {
    type: Number,
    default: 7
  }
})

// 步骤配置
const steps = [
  { id: 1, label: '灵魂之门', icon: 'door-open' },
  { id: 2, label: '情感共鸣', icon: 'heart' },
  { id: 3, label: '主题焦点', icon: 'target' },
  { id: 4, label: '叙事艺术', icon: 'book-open' },
  { id: 5, label: '视觉美学', icon: 'palette' },
  { id: 6, label: '角色灵魂', icon: 'user' },
  { id: 7, label: '生成画像', icon: 'sparkles' },
]

// 判断步骤状态
const getStepStatus = (step) => {
  if (step.id < props.currentStep) return 'completed'
  if (step.id === props.currentStep) return 'current'
  return 'upcoming'
}

// 步骤图标样式
const getStepIconClass = (step) => {
  const status = getStepStatus(step)
  const classes = []
  
  if (status === 'completed') {
    classes.push('completed-icon')
  } else if (status === 'current') {
    classes.push('current-icon')
  } else {
    classes.push('upcoming-icon')
  }
  
  return classes.join(' ')
}

// 步骤标签样式
const getStepLabelClass = (step) => {
  const status = getStepStatus(step)
  const classes = []
  
  if (status === 'completed') {
    classes.push('completed-label')
  } else if (status === 'current') {
    classes.push('current-label')
  } else {
    classes.push('upcoming-label')
  }
  
  return classes.join(' ')
}

// 连接线样式
const getConnectorClass = (step) => {
  return step.id < props.currentStep ? 'completed-connector' : 'upcoming-connector'
}


</script>

<style scoped>
/* 容器样式 */
.step-container {
  @apply flex flex-col items-center gap-3 w-32 relative;
}

.step-container.current-step {
  animation: float 3s ease-in-out infinite;
}

/* 步骤内容 */
.step-content {
  @apply flex flex-col items-center gap-3;
}

/* 图标样式 */
.step-icon {
  @apply w-8 h-8 rounded-full flex items-center justify-center transition-all duration-300;
}

.completed-icon {
  @apply bg-brand-purple text-white shadow-[0_0_15px_rgba(147,51,234,0.6)];
}

.current-icon {
  @apply border-2 border-brand-purple text-brand-purple bg-brand-black;
  animation: pulse-glow 2s ease-in-out infinite;
}

.upcoming-icon {
  @apply border border-white/30 text-white/50;
}

.icon {
  @apply w-4 h-4;
}

/* 标签样式 */
.step-label {
  @apply text-[10px] tracking-widest transition-colors duration-300;
}

.completed-label {
  @apply text-white/90;
}

.current-label {
  @apply text-brand-purple font-bold;
}

.upcoming-label {
  @apply text-white/50 opacity-40;
}

/* 连接线样式 */
.connector {
  @apply h-[1px] flex-1 absolute top-4 left-[calc(100%-2rem)] w-[calc(100%-4rem)] transition-colors duration-300;
}

.completed-connector {
  @apply bg-brand-purple shadow-[0_0_5px_rgba(147,51,234,0.5)];
}

.upcoming-connector {
  @apply bg-white/10;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .step-container {
    @apply w-24;
  }
  
  .connector {
    @apply left-[calc(100%-1.5rem)] w-[calc(100%-3rem)];
  }
}

@media (max-width: 768px) {
  .step-container {
    @apply w-20;
  }
  
  .step-icon {
    @apply w-6 h-6;
  }
  
  .icon {
    @apply w-3 h-3;
  }
  
  .step-label {
    @apply text-[8px];
  }
  
  .connector {
    @apply left-[calc(100%-1rem)] w-[calc(100%-2rem)];
  }
}
</style>