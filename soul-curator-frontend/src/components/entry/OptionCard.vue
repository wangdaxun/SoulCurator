<template>
  <shatter-effect ref="shatterRef">
    <div 
      class="option-card"
      :class="cardClasses"
      role="button"
      :tabindex="isSelectable ? 0 : -1"
      :aria-label="`选择选项：${option.title}`"
      :aria-pressed="isSelected"
      :aria-disabled="!isSelectable"
      @click="handleSelect"
      @keydown.enter="handleSelect"
      @keydown.space="handleSelect"
      @mouseenter="isHovered = true"
      @mouseleave="isHovered = false"
      @focus="isFocused = true"
      @blur="isFocused = false"
    >
      <!-- 选项头部 -->
      <div class="option-header">
        <h3 class="option-title">{{ option.title }}</h3>
        
        <!-- 选中图标 -->
        <div 
          v-if="isSelected"
          class="check-icon"
          aria-hidden="true"
        >
          <Icon name="check-circle-2" :size="20" />
        </div>
      </div>
      
      <!-- 选项描述 -->
      <p class="option-description">{{ option.description }}</p>
      
      <!-- 影片参考 -->
      <div class="option-references">
        <span class="reference-label">影片参考：</span>
        <div class="tags-container">
          <Tag
            v-for="reference in option.references"
            :key="reference"
            :text="reference"
            context="reference"
            :glow="false"
            class="reference-tag"
          />
        </div>
      </div>
      
      <!-- 选中状态装饰 -->
      <div 
        v-if="isSelected"
        class="selection-decoration"
        aria-hidden="true"
      >
        <div class="glow-effect"></div>
      </div>
    </div>
  </shatter-effect>
</template>

<script setup>
import { ref, computed } from 'vue'
import ShatterEffect from './ShatterEffect.vue'
import Icon from "@/components/shared/Icon.vue"
import Tag from '@/components/shared/Tag.vue'

const props = defineProps({
  option: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.id && value.title && value.description && Array.isArray(value.references)
    }
  },
  isSelected: {
    type: Boolean,
    default: false
  },
  isSelectable: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['select'])

// 交互状态
const isHovered = ref(false)
const isFocused = ref(false)

const shatterRef = ref(null)

// 卡片类名
const cardClasses = computed(() => {
  const classes = ['option-card-base']
  
  if (props.isSelected) {
    classes.push('option-card-selected')
  } else if (isHovered.value && props.isSelectable) {
    classes.push('option-card-hover')
  }
  
  if (isFocused.value) {
    classes.push('option-card-focused')
  }
  
  if (!props.isSelectable) {
    classes.push('option-card-disabled')
  }
  
  return classes.join(' ')
})

// 处理选择
const handleSelect = () => {
  if (props.isSelectable) {
    shatterRef.value?.trigger()
    setTimeout(() => {
      emit('select', props.option.id)
    }, 500) // 等待碎片动画完成
  }
}
</script>

<style scoped>
/* 基础卡片 */
.option-card-base {
  @apply p-6 rounded-xl border border-white/10 cursor-pointer transition-all duration-300;
  transition-timing-function: cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
}

/* 悬停状态 */
.option-card-hover {
  @apply bg-white/5 border-white/30 transform -translate-y-1;
  box-shadow: 
    0 10px 25px -5px rgba(0, 0, 0, 0.5),
    0 0 15px rgba(147, 51, 234, 0.2);
}

/* 选中状态 */
.option-card-selected {
  @apply bg-brand-purple/15 border-brand-purple;
  box-shadow: 
    0 15px 30px -10px rgba(0, 0, 0, 0.6),
    0 0 25px rgba(147, 51, 234, 0.3),
    inset 0 0 20px rgba(147, 51, 234, 0.1);
  transform: translateY(-2px);
}

/* 焦点状态 */
.option-card-focused {
  @apply ring-2 ring-brand-purple/50 ring-offset-2 ring-offset-brand-black;
  outline: none;
}

/* 禁用状态 */
.option-card-disabled {
  @apply opacity-40 cursor-not-allowed pointer-events-none;
}

/* 选项头部 */
.option-header {
  @apply flex justify-between items-start mb-3;
}

.option-title {
  @apply text-lg font-bold text-white transition-colors duration-300;
}

.option-card-hover .option-title {
  @apply text-brand-accent;
}

.option-card-selected .option-title {
  @apply text-white;
}

/* 选中图标 */
.check-icon {
  @apply text-brand-purple;
  animation: check-appear 0.3s ease-out;
}

@keyframes check-appear {
  0% {
    opacity: 0;
    transform: scale(0.5) rotate(-90deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0);
  }
}

/* 选项描述 */
.option-description {
  @apply text-sm text-white/60 mb-6 leading-relaxed;
  min-height: 3.5rem;
  text-align: left;
}

.option-card-selected .option-description {
  @apply text-white/80;
}

/* 影片参考 */
.option-references {
  @apply flex items-center gap-2;
}

.reference-label {
  @apply text-[10px] text-white/30 whitespace-nowrap;
}

.tags-container {
  @apply flex flex-wrap gap-1;
}

.reference-tag {
  @apply transition-opacity duration-200;
}

.option-card-hover .reference-tag {
  @apply opacity-80;
}

.option-card-selected .reference-tag {
  @apply opacity-100;
}

/* 选中装饰 */
.selection-decoration {
  @apply absolute inset-0 pointer-events-none;
}

.glow-effect {
  @apply absolute inset-0;
  background: radial-gradient(
    circle at center,
    rgba(147, 51, 234, 0.1) 0%,
    transparent 70%
  );
  animation: glow-pulse 2s ease-in-out infinite;
}

@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.8;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .option-card-base {
    @apply p-4;
  }
  
  .option-title {
    @apply text-base;
  }
  
  .option-description {
    @apply text-xs mb-4;
    min-height: 3rem;
  }
  
  .option-references {
    @apply flex-col items-start gap-1;
  }
}
</style>