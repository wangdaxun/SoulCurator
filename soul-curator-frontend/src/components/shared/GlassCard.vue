<template>
  <div 
    class="glass-card"
    :class="customClass"
    :style="customStyle"
    ref="cardRef"
  >
    <slot />
    
    <!-- 可选的装饰效果 -->
    <div 
      v-if="showGlow"
      class="glass-glow"
      :class="glowClass"
      aria-hidden="true"
    ></div>
  </div>
</template>

<script setup>
import { defineProps, computed, ref } from 'vue'

const props = defineProps({
  // 自定义类名
  customClass: {
    type: String,
    default: ''
  },
  // 是否显示发光效果
  showGlow: {
    type: Boolean,
    default: false
  },
  // 发光效果类型
  glowType: {
    type: String,
    default: 'purple', // 'purple' | 'accent' | 'white'
    validator: (value) => ['purple', 'accent', 'white'].includes(value)
  },
  // 是否启用悬停效果
  hoverable: {
    type: Boolean,
    default: true
  },
  // 自定义样式
  customStyle: {
    type: Object,
    default: () => ({})
  },
  // 边框强度
  borderIntensity: {
    type: Number,
    default: 0.3,
    validator: (value) => value >= 0 && value <= 1
  },
  // 模糊强度
  blurIntensity: {
    type: Number,
    default: 12,
    validator: (value) => value >= 0 && value <= 50
  }
})

// 卡片DOM引用
const cardRef = ref(null)

// 计算发光效果类名
const glowClass = computed(() => {
  switch (props.glowType) {
    case 'accent':
      return 'glow-accent'
    case 'white':
      return 'glow-white'
    default:
      return 'glow-purple'
  }
})

// 计算动态样式
const computedStyle = computed(() => {
  const style = { ...props.customStyle }
  
  // 动态边框透明度
  if (props.borderIntensity !== 0.3) {
    style.borderColor = `rgba(147, 51, 234, ${props.borderIntensity})`
  }
  
  // 动态模糊强度
  if (props.blurIntensity !== 12) {
    style.backdropFilter = `blur(${props.blurIntensity}px)`
    style.webkitBackdropFilter = `blur(${props.blurIntensity}px)`
  }
  
  return style
})

// 暴露方法给父组件
defineExpose({
  // 获取卡片DOM元素
  getElement: () => cardRef.value,
  
  // 添加临时高亮效果
  highlight: () => {
    if (cardRef.value) {
      cardRef.value.classList.add('highlight-temp')
      setTimeout(() => {
        if (cardRef.value) {
          cardRef.value.classList.remove('highlight-temp')
        }
      }, 1000)
    }
  },
  
  // 添加震动效果
  shake: () => {
    if (cardRef.value) {
      cardRef.value.classList.add('shake-temp')
      setTimeout(() => {
        if (cardRef.value) {
          cardRef.value.classList.remove('shake-temp')
        }
      }, 500)
    }
  }
})
</script>

<style scoped>
/* 基础玻璃态卡片 */
.glass-card {
  @apply rounded-[20px] relative;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(147, 51, 234, 0.3);
  box-shadow: 
    0 8px 32px 0 rgba(0, 0, 0, 0.8),
    inset 0 0 15px rgba(147, 51, 234, 0.05);
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 悬停效果 */
.glass-card.hoverable:hover {
  @apply transform -translate-y-1;
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(147, 51, 234, 0.5);
  box-shadow: 
    0 12px 40px 0 rgba(0, 0, 0, 0.9),
    inset 0 0 20px rgba(147, 51, 234, 0.1);
}

/* 发光效果 */
.glass-glow {
  @apply absolute inset-0 rounded-[20px] pointer-events-none opacity-0;
  transition: opacity 0.3s ease;
}

.glass-card:hover .glass-glow {
  @apply opacity-100;
}

/* 紫色发光 */
.glow-purple {
  background: radial-gradient(
    circle at center,
    rgba(147, 51, 234, 0.15) 0%,
    transparent 70%
  );
  box-shadow: 
    inset 0 0 30px rgba(147, 51, 234, 0.1),
    0 0 40px rgba(147, 51, 234, 0.2);
}

/* 强调色发光 */
.glow-accent {
  background: radial-gradient(
    circle at center,
    rgba(192, 132, 252, 0.15) 0%,
    transparent 70%
  );
  box-shadow: 
    inset 0 0 30px rgba(192, 132, 252, 0.1),
    0 0 40px rgba(192, 132, 252, 0.2);
}

/* 白色发光 */
.glow-white {
  background: radial-gradient(
    circle at center,
    rgba(255, 255, 255, 0.1) 0%,
    transparent 70%
  );
  box-shadow: 
    inset 0 0 30px rgba(255, 255, 255, 0.05),
    0 0 40px rgba(255, 255, 255, 0.1);
}

/* 临时高亮效果 */
.highlight-temp {
  animation: highlight-pulse 1s ease-out;
}

@keyframes highlight-pulse {
  0% {
    box-shadow: 
      0 8px 32px 0 rgba(0, 0, 0, 0.8),
      inset 0 0 15px rgba(147, 51, 234, 0.05),
      0 0 0 0 rgba(147, 51, 234, 0.5);
  }
  50% {
    box-shadow: 
      0 8px 32px 0 rgba(0, 0, 0, 0.8),
      inset 0 0 15px rgba(147, 51, 234, 0.05),
      0 0 30px 10px rgba(147, 51, 234, 0.3);
  }
  100% {
    box-shadow: 
      0 8px 32px 0 rgba(0, 0, 0, 0.8),
      inset 0 0 15px rgba(147, 51, 234, 0.05),
      0 0 0 0 rgba(147, 51, 234, 0.5);
  }
}

/* 临时震动效果 */
.shake-temp {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-2px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(2px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .glass-card {
    @apply rounded-[16px];
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
  }
  
  .glass-glow {
    @apply rounded-[16px];
  }
}

/* 减少运动偏好 */
@media (prefers-reduced-motion: reduce) {
  .glass-card {
    transition: none;
  }
  
  .glass-card.hoverable:hover {
    transform: none;
  }
  
  .highlight-temp,
  .shake-temp {
    animation: none;
  }
}
</style>