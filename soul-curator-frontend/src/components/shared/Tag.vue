<template>
  <div 
    class="soul-tag"
    :class="[effectiveSize, { 'glow-effect': glow, 'clickable': clickable }]"
    :style="tagStyle"
    @click="handleClick"
  >
    <!-- 图标区域 -->
    <Icon 
      v-if="icon"
      :name="icon"
      :size="iconSize"
      :color="iconColor"
      class="tag-icon"
    />
    
    <!-- 文字区域 -->
    <span class="tag-text">{{ text }}</span>
    
    <!-- 关闭按钮（可选） -->
    <Icon 
      v-if="closable"
      name="x"
      :size="12"
      color="currentColor"
      class="close-icon"
      @click.stop="handleClose"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  // 基础属性
  text: {
    type: String,
    required: true
  },
  icon: {
    type: String,
    default: ''
  },
  
  // 样式属性
  color: {
    type: String,
    default: '#8b5cf6' // tech-purple
  },
  backgroundColor: {
    type: String,
    default: 'rgba(255, 255, 255, 0.03)'
  },
  borderColor: {
    type: String,
    default: 'rgba(255, 255, 255, 0.1)'
  },
  
  // 效果属性
  glow: {
    type: Boolean,
    default: false
  },
  glowColor: {
    type: String,
    default: '' // 默认使用color
  },
  glowIntensity: {
    type: Number,
    default: 0.4 // 发光强度 0-1
  },
  
  // 交互属性
  clickable: {
    type: Boolean,
    default: false
  },
  closable: {
    type: Boolean,
    default: false
  },
  
  // 尺寸属性
  size: {
    type: String,
    default: 'medium', // small, medium, large
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  
  // 上下文感知（自动调整尺寸）
  context: {
    type: String,
    default: '', // portrait, reference, default
    validator: (value) => ['', 'portrait', 'reference', 'default'].includes(value)
  },
  
  // 自定义样式
  customClass: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['click', 'close'])

// 计算属性
const tagStyle = computed(() => {
  const glowColor = props.glowColor || props.color
  
  return {
    '--tag-color': props.color,
    '--tag-bg-color': props.backgroundColor,
    '--tag-border-color': props.borderColor,
    '--tag-glow-color': glowColor,
    '--tag-glow-intensity': props.glowIntensity
  }
})

// 根据上下文自动调整尺寸
const effectiveSize = computed(() => {
  if (props.size !== 'medium') return props.size // 如果明确指定了尺寸，优先使用
  
  // 根据上下文自动调整
  switch (props.context) {
    case 'portrait':
      return 'medium' // 灵魂画像页面保持中等大小
    case 'reference':
      return 'small'  // 影片参考页面使用小尺寸
    default:
      return props.size
  }
})

const iconSize = computed(() => {
  const sizeMap = {
    small: 12,
    medium: 16,
    large: 18
  }
  return sizeMap[effectiveSize.value] || 16
})

const iconColor = computed(() => props.color)

// 事件处理
const handleClick = () => {
  if (props.clickable) {
    emit('click')
  }
}

const handleClose = (e) => {
  e.stopPropagation()
  emit('close')
}
</script>

<style scoped>
.soul-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 16px;
  background: var(--tag-bg-color, rgba(255, 255, 255, 0.03));
  backdrop-filter: blur(20px);
  border: 1px solid var(--tag-border-color, rgba(255, 255, 255, 0.1));
  transition: all 0.3s ease;
  cursor: default;
  position: relative;
  z-index: 1;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

/* 发光效果 */
.soul-tag.glow-effect:hover {
  box-shadow: 0 0 20px 
    rgba(
      var(--tag-glow-color-rgb, 139, 92, 246), 
      var(--tag-glow-intensity, 0.4)
    );
  border-color: rgba(
    var(--tag-glow-color-rgb, 139, 92, 246), 
    calc(var(--tag-glow-intensity, 0.4) + 0.2)
  );
  transform: translateY(-2px);
}

/* 可点击状态 */
.soul-tag.clickable {
  cursor: pointer;
}

.soul-tag.clickable:hover {
  background: rgba(255, 255, 255, 0.05);
}

/* 图标样式 */
.tag-icon {
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.soul-tag:hover .tag-icon {
  transform: scale(1.1);
}

/* 文字样式 */
.tag-text {
  font-weight: 500;
  font-size: 14px;
  color: #e2e8f0;
  white-space: nowrap;
}

/* 关闭按钮 */
.close-icon {
  margin-left: 4px;
  opacity: 0.6;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.close-icon:hover {
  opacity: 1;
}

/* 尺寸变体 */
.soul-tag.small {
  padding: 4px 12px;
  border-radius: 12px;
}

.soul-tag.small .tag-text {
  font-size: 12px;
}

.soul-tag.large {
  padding: 12px 24px;
  border-radius: 20px;
}

.soul-tag.large .tag-text {
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .soul-tag {
    padding: 6px 12px;
  }
  
  .tag-text {
    font-size: 13px;
  }
}
</style>