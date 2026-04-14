<template>
  <div 
    class="quote-card"
    :class="{ 'interactive': interactive }"
    :style="cardStyle"
    @click="handleClick"
  >
    <!-- 装饰性引号图标 -->
    <div class="quote-decoration">
      <Icon name="quote" :size="80" color="rgba(255, 255, 255, 0.05)" />
    </div>
    
    <!-- 引用内容 -->
    <div class="quote-content">
      <p class="quote-text">{{ text }}</p>
      
      <!-- 作者信息 -->
      <div class="quote-author">
        <div class="author-line"></div>
        <span class="author-name">{{ author }}</span>
        <span v-if="source" class="author-source"> · {{ source }}</span>
      </div>
      
      <!-- 额外信息（可选） -->
      <div v-if="additionalInfo" class="additional-info">
        <Icon name="info" :size="12" color="rgba(255, 255, 255, 0.4)" />
        <span class="info-text">{{ additionalInfo }}</span>
      </div>
    </div>
    
    <!-- 交互按钮（可选） -->
    <div v-if="showActions" class="quote-actions">
      <button class="action-button" @click.stop="handleCopy">
        <Icon name="copy" :size="14" />
        <span>复制</span>
      </button>
      <button class="action-button" @click.stop="handleShare">
        <Icon name="share-2" :size="14" />
        <span>分享</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  // 核心内容
  text: {
    type: String,
    required: true
  },
  author: {
    type: String,
    required: true
  },
  source: {
    type: String,
    default: '' // 如：书籍、电影、歌曲名
  },
  
  // 样式配置
  theme: {
    type: String,
    default: 'default', // default, dark, light, gradient
    validator: (value) => ['default', 'dark', 'light', 'gradient'].includes(value)
  },
  accentColor: {
    type: String,
    default: '#fbbf24' // particle-gold
  },
  backgroundColor: {
    type: String,
    default: '' // 自动根据theme生成
  },
  
  // 交互功能
  interactive: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: false
  },
  additionalInfo: {
    type: String,
    default: '' // 如：出处、背景信息
  },
  
  // 动画效果
  animate: {
    type: Boolean,
    default: true
  },
  animationDelay: {
    type: Number,
    default: 0 // 延迟动画（ms）
  }
})

const emit = defineEmits(['click', 'copy', 'share'])

// 计算样式
const cardStyle = computed(() => {
  const style = {}
  
  // 根据主题设置背景
  if (props.backgroundColor) {
    style.background = props.backgroundColor
  } else {
    switch (props.theme) {
      case 'dark':
        style.background = 'rgba(0, 0, 0, 0.3)'
        break
      case 'light':
        style.background = 'rgba(255, 255, 255, 0.1)'
        break
      case 'gradient':
        style.background = `linear-gradient(135deg, rgba(${hexToRgb(props.accentColor)}, 0.1) 0%, rgba(251, 191, 36, 0.05) 100%)`
        break
      default:
        style.background = 'linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(251, 191, 36, 0.05) 100%)'
    }
  }
  
  // 动画延迟
  if (props.animate && props.animationDelay > 0) {
    style.animationDelay = `${props.animationDelay}ms`
  }
  
  // 强调色
  // style.setProperty('--accent-color', props.accentColor)
  
  return style
})

// 工具函数：十六进制转RGB
const hexToRgb = (hex) => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result 
    ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`
    : '139, 92, 246' // 默认tech-purple
}

// 事件处理
const handleClick = () => {
  if (props.interactive) {
    emit('click')
  }
}

const handleCopy = async () => {
  try {
    await navigator.clipboard.writeText(`${props.text} — ${props.author}`)
    emit('copy', true)
  } catch (err) {
    emit('copy', false)
    console.error('复制失败:', err)
  }
}

const handleShare = () => {
  emit('share')
}
</script>

<style scoped>
.quote-card {
  border-radius: 24px;
  padding: 32px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

/* 交互效果 */
.quote-card.interactive:hover {
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

/* 装饰性引号 */
.quote-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  transform: rotate(-12deg);
  transition: transform 0.7s ease;
  z-index: 0;
}

.quote-card:hover .quote-decoration {
  transform: rotate(0deg);
}

/* 内容区域 */
.quote-content {
  position: relative;
  z-index: 1;
}

/* 引用文字 */
.quote-text {
  font-size: 20px;
  font-style: italic;
  line-height: 1.6;
  color: #e2e8f0;
  margin-bottom: 24px;
  font-weight: 300;
  position: relative;
  text-align: left;
}

/* 作者信息 */
.quote-author {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.author-line {
  width: 48px;
  height: 1px;
  background: rgba(var(--accent-color, 251, 191, 36), 0.5);
}

.author-name {
  font-size: 14px;
  color: var(--accent-color, #fbbf24);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-weight: 700;
}

.author-source {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-style: italic;
}

/* 额外信息 */
.additional-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
}

.info-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

/* 交互按钮 */
.quote-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.action-button:active {
  transform: scale(0.95);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.quote-card {
  animation: fadeInUp 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quote-card {
    padding: 24px;
    border-radius: 20px;
  }
  
  .quote-text {
    font-size: 18px;
  }
  
  .quote-decoration {
    top: -10px;
    right: -10px;
  }
  
  .quote-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .quote-card {
    padding: 20px;
  }
  
  .quote-text {
    font-size: 16px;
  }
  
  .author-name {
    font-size: 12px;
  }
}
</style>