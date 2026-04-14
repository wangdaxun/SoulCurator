<template>
  <div 
    class="recommendation-card"
    :class="{ 'interactive': interactive, 'loading': isLoading }"
    @click="handleClick"
  >
    <!-- 图片区域 -->
    <div class="card-image-container">
      <img 
        v-if="imageUrl && !isLoading" 
        :src="imageUrl" 
        :alt="title" 
        class="card-image"
        @load="handleImageLoad"
        @error="handleImageError"
      />
      
      <!-- 加载状态 -->
      <div v-if="isLoading" class="image-loading">
        <Icon name="loader-2" :size="24" color="rgba(255, 255, 255, 0.3)" class="loading-spinner" />
      </div>
      
      <!-- 图片遮罩 -->
      <div class="image-overlay"></div>
      
      <!-- 类型标签 -->
      <div class="type-badge">
        <Icon :name="typeIcon" :size="12" color="currentColor" />
        <span class="type-text">{{ typeText }}</span>
      </div>
      
      <!-- 收藏按钮（可选） -->
      <button 
        v-if="showFavorite" 
        class="favorite-button"
        @click.stop="toggleFavorite"
        :aria-label="isFavorite ? '取消收藏' : '收藏'"
      >
        <Icon 
          :name="isFavorite ? 'heart' : 'heart-outline'" 
          :size="16" 
          :color="isFavorite ? '#ef4444' : 'rgba(255, 255, 255, 0.7)'"
          :fill="isFavorite ? '#ef4444' : 'none'"
        />
      </button>
    </div>
    
    <!-- 内容区域 -->
    <div class="card-content">
      <!-- 标题 -->
      <h4 class="card-title">{{ title }}</h4>
      
      <!-- 描述 -->
      <p class="card-description">{{ description }}</p>
      
      <!-- 元信息 -->
      <div v-if="showMeta" class="card-meta">
        <!-- 评分（可选） -->
        <div v-if="rating" class="meta-rating">
          <Icon name="star" :size="12" color="#fbbf24" fill="#fbbf24" />
          <span class="rating-value">{{ rating.toFixed(1) }}</span>
        </div>
        
        <!-- 时长/页数（可选） -->
        <div v-if="duration" class="meta-duration">
          <Icon name="clock" :size="12" color="rgba(255, 255, 255, 0.5)" />
          <span class="duration-text">{{ duration }}</span>
        </div>
        
        <!-- 年份（可选） -->
        <div v-if="year" class="meta-year">
          <Icon name="calendar" :size="12" color="rgba(255, 255, 255, 0.5)" />
          <span class="year-text">{{ year }}</span>
        </div>
      </div>
      
      <!-- 行动按钮 -->
      <div v-if="showActions" class="card-actions">
        <button class="action-button primary" @click.stop="handleView">
          <Icon name="eye" :size="14" />
          <span>查看详情</span>
        </button>
        <button class="action-button secondary" @click.stop="handleSave">
          <Icon name="bookmark" :size="14" />
          <span>稍后看</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  // 核心内容
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'movie', // movie, music, book, game, art, etc.
    validator: (value) => ['movie', 'music', 'book', 'game', 'art', 'other'].includes(value)
  },
  imageUrl: {
    type: String,
    default: ''
  },
  
  // 元信息
  rating: {
    type: Number,
    default: 0,
    validator: (value) => value >= 0 && value <= 5
  },
  duration: {
    type: String,
    default: '' // 如：'2h 30m', '320页', '4:35'
  },
  year: {
    type: [String, Number],
    default: ''
  },
  
  // 交互配置
  interactive: {
    type: Boolean,
    default: true
  },
  showFavorite: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: false
  },
  showMeta: {
    type: Boolean,
    default: false
  },
  
  // 状态
  isFavorite: {
    type: Boolean,
    default: false
  },
  
  // 样式配置
  size: {
    type: String,
    default: 'medium', // small, medium, large
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  theme: {
    type: String,
    default: 'default' // default, compact, detailed
  }
})

const emit = defineEmits([
  'click', 
  'favorite-toggle', 
  'view', 
  'save',
  'image-load',
  'image-error'
])

// 状态
const isLoading = ref(true)
const imageError = ref(false)

// 计算属性
const typeText = computed(() => {
  const typeMap = {
    movie: '电影推荐',
    music: '音乐曲目',
    book: '文学著作',
    game: '游戏推荐',
    art: '艺术作品',
    other: '推荐'
  }
  return typeMap[props.type] || '推荐'
})

const typeIcon = computed(() => {
  const iconMap = {
    movie: 'film',
    music: 'music',
    book: 'book-open',
    game: 'gamepad-2',
    art: 'palette',
    other: 'sparkles'
  }
  return iconMap[props.type] || 'sparkles'
})

const typeColor = computed(() => {
  const colorMap = {
    movie: '#8b5cf6', // tech-purple
    music: '#10b981', // emerald
    book: '#f59e0b', // amber
    game: '#ef4444', // red
    art: '#ec4899', // pink
    other: '#64748b' // slate
  }
  return colorMap[props.type] || '#8b5cf6'
})

// 事件处理
const handleClick = () => {
  if (props.interactive) {
    emit('click', props)
  }
}

const toggleFavorite = () => {
  emit('favorite-toggle', !props.isFavorite)
}

const handleView = () => {
  emit('view', props)
}

const handleSave = () => {
  emit('save', props)
}

const handleImageLoad = () => {
  isLoading.value = false
  imageError.value = false
  emit('image-load')
}

const handleImageError = () => {
  isLoading.value = false
  imageError.value = true
  emit('image-error')
}

// 生命周期
onMounted(() => {
  // 如果没有图片URL，直接标记为加载完成
  if (!props.imageUrl) {
    isLoading.value = false
  }
})
</script>

<style scoped>
.recommendation-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

/* 交互效果 */
.recommendation-card.interactive:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

/* 图片容器 */
.card-image-container {
  aspect-ratio: 16/9;
  background: rgba(99, 102, 241, 0.3);
  border-radius: 12px 12px 0 0;
  margin: 0;
  overflow: hidden;
  position: relative;
}

/* 图片 */
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.6;
  transition: transform 0.7s ease;
}

.recommendation-card:hover .card-image {
  transform: scale(1.1);
}

/* 加载状态 */
.image-loading {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.5);
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 图片遮罩 */
.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, #020617, transparent);
}

/* 类型标签 */
.type-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: v-bind(typeColor);
  font-weight: 700;
}

/* 收藏按钮 */
.favorite-button {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.favorite-button:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
}

/* 内容区域 */
.card-content {
  padding: 16px;
}

/* 标题 */
.card-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
  line-height: 1.2;
  color: #e2e8f0;
}

/* 描述 */
.card-description {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

/* 元信息 */
.card-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.meta-rating,
.meta-duration,
.meta-year {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
}

.rating-value {
  color: #fbbf24;
  font-weight: 600;
}

/* 行动按钮 */
.card-actions {
  display: flex;
  gap: 8px;
}

.action-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.action-button.primary {
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
  color: #c4b5fd;
}

.action-button.primary:hover {
  background: rgba(139, 92, 246, 0.3);
  color: #ffffff;
}

.action-button.secondary {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
}

.action-button.secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

/* 尺寸变体 */
.recommendation-card.small .card-content {
  padding: 12px;
}

.recommendation-card.small .card-title {
  font-size: 14px;
}

.recommendation-card.small .card-description {
  font-size: 11px;
  -webkit-line-clamp: 1;
}

.recommendation-card.large .card-content {
  padding: 20px;
}

.recommendation-card.large .card-title {
  font-size: 20px;
}

.recommendation-card.large .card-description {
  font-size: 14px;
  -webkit-line-clamp: 3;
}

/* 主题变体 */
.recommendation-card.compact .card-image-container {
  aspect-ratio: 1/1;
  border-radius: 12px;
  margin: 12px 12px 0 12px;
}

.recommendation-card.compact .card-content {
  padding: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .recommendation-card {
    border-radius: 12px;
  }
  
  .card-image-container {
    aspect-ratio: 4/3;
  }
  
  .card-title {
    font-size: 16px;
  }
}
</style>