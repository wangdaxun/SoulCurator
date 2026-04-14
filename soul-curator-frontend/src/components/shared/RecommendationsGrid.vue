<template>
  <div class="">
    <!-- 标题区域 -->
    <div class="grid-header" v-if="showHeader">
      <div class="header-left">
        <h3 class="grid-title">
          <Icon v-if="titleIcon" :name="titleIcon" :size="20" :color="titleIconColor" />
          {{ title }}
        </h3>
        <p v-if="subtitle" class="grid-subtitle">{{ subtitle }}</p>
      </div>
      
      <div class="header-right" v-if="showControls">
        <!-- 布局切换 -->
        <div v-if="showLayoutToggle" class="layout-toggle">
          <button 
            class="layout-button"
            :class="{ 'active': layout === 'grid' }"
            @click="changeLayout('grid')"
            aria-label="网格布局"
          >
            <Icon name="grid" :size="14" />
          </button>
          <button 
            class="layout-button"
            :class="{ 'active': layout === 'list' }"
            @click="changeLayout('list')"
            aria-label="列表布局"
          >
            <Icon name="list" :size="14" />
          </button>
        </div>
        
        <!-- 刷新按钮 -->
        <button 
          v-if="showRefresh" 
          class="refresh-button"
          @click="handleRefresh"
          :disabled="isRefreshing"
        >
          <Icon 
            name="refresh-cw" 
            :size="16" 
            :class="{ 'refreshing': isRefreshing }"
          />
          <span v-if="showRefreshText">换一批</span>
        </button>
        
        <!-- 查看更多 -->
        <button 
          v-if="showViewAll" 
          class="view-all-button"
          @click="handleViewAll"
        >
          查看更多
          <Icon name="chevron-right" :size="14" />
        </button>
      </div>
    </div>
    
    <!-- 推荐内容 -->
    <div class="grid-content" :class="[layout, `cols-${columns}`]">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <div class="loading-spinner">
          <Icon name="loader-2" :size="32" color="#8b5cf6" />
        </div>
        <p class="loading-text">正在加载推荐...</p>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="!recommendations || recommendations.length === 0" class="empty-state">
        <Icon name="search" :size="48" color="rgba(255, 255, 255, 0.2)" />
        <p class="empty-text">{{ emptyText }}</p>
        <button v-if="showRefresh" class="refresh-button" @click="handleRefresh">
          重新加载
        </button>
      </div>
      
      <!-- 推荐卡片 -->
      <template v-else>
        <RecommendationCard
          v-for="(item, index) in paginatedRecommendations"
          :key="item.id || `${item.title}-${index}`"
          :title="item.title"
          :description="item.description"
          :type="item.type"
          :image-url="item.imageUrl"
          :rating="item.rating"
          :duration="item.duration"
          :year="item.year"
          :interactive="interactive"
          :show-favorite="showFavorite"
          :show-actions="showActions"
          :show-meta="showMeta"
          :is-favorite="isItemFavorite(item)"
          :size="cardSize"
          :theme="cardTheme"
          @click="handleCardClick(item, index)"
          @favorite-toggle="(fav) => handleFavoriteToggle(item, fav)"
          @view="() => handleCardView(item)"
          @save="() => handleCardSave(item)"
        />
      </template>
    </div>
    
    <!-- 分页控制 -->
    <div v-if="showPagination && totalPages > 1" class="pagination-controls">
      <button 
        class="pagination-button prev"
        @click="prevPage"
        :disabled="currentPage === 1"
      >
        <Icon name="chevron-left" :size="16" />
        上一页
      </button>
      
      <div class="page-indicator">
        <span class="current-page">{{ currentPage }}</span>
        <span class="page-separator">/</span>
        <span class="total-pages">{{ totalPages }}</span>
      </div>
      
      <button 
        class="pagination-button next"
        @click="nextPage"
        :disabled="currentPage === totalPages"
      >
        下一页
        <Icon name="chevron-right" :size="16" />
      </button>
    </div>
    
    <!-- 加载更多 -->
    <div v-if="showLoadMore && hasMore" class="load-more">
      <button 
        class="load-more-button"
        @click="handleLoadMore"
        :disabled="isLoadingMore"
      >
        <Icon 
          v-if="isLoadingMore" 
          name="loader-2" 
          :size="16" 
          class="loading-spinner"
        />
        <span v-else>加载更多</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Icon from './Icon.vue'
import RecommendationCard from './RecommendationCard.vue'

const props = defineProps({
  // 数据
  recommendations: {
    type: Array,
    default: () => []
  },
  
  // 标题配置
  title: {
    type: String,
    default: '今日灵魂共鸣'
  },
  subtitle: {
    type: String,
    default: ''
  },
  titleIcon: {
    type: String,
    default: 'zap'
  },
  titleIconColor: {
    type: String,
    default: '#fbbf24'
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  
  // 布局配置
  layout: {
    type: String,
    default: 'grid', // grid, list
    validator: (value) => ['grid', 'list'].includes(value)
  },
  columns: {
    type: Number,
    default: 3,
    validator: (value) => value >= 1 && value <= 6
  },
  cardSize: {
    type: String,
    default: 'medium' // small, medium, large
  },
  cardTheme: {
    type: String,
    default: 'default' // default, compact, detailed
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
  
  // 控制配置
  showControls: {
    type: Boolean,
    default: true
  },
  showLayoutToggle: {
    type: Boolean,
    default: false
  },
  showRefresh: {
    type: Boolean,
    default: true
  },
  showRefreshText: {
    type: Boolean,
    default: true
  },
  showViewAll: {
    type: Boolean,
    default: false
  },
  
  // 分页配置
  showPagination: {
    type: Boolean,
    default: false
  },
  pageSize: {
    type: Number,
    default: 6
  },
  showLoadMore: {
    type: Boolean,
    default: false
  },
  loadMoreSize: {
    type: Number,
    default: 3
  },
  
  // 状态
  isLoading: {
    type: Boolean,
    default: false
  },
  isRefreshing: {
    type: Boolean,
    default: false
  },
  favorites: {
    type: Array,
    default: () => []
  },
  
  // 文本配置
  emptyText: {
    type: String,
    default: '暂无推荐内容'
  }
})

const emit = defineEmits([
  'refresh',
  'view-all',
  'card-click',
  'favorite-toggle',
  'card-view',
  'card-save',
  'layout-change',
  'page-change',
  'load-more'
])

// 状态
const currentPage = ref(1)
const displayedCount = ref(props.pageSize)
const isLoadingMore = ref(false)
const internalLayout = ref(props.layout)

// 计算属性
const totalPages = computed(() => {
  if (!props.showPagination) return 1
  return Math.ceil(props.recommendations.length / props.pageSize)
})

const paginatedRecommendations = computed(() => {
  if (props.showPagination) {
    const start = (currentPage.value - 1) * props.pageSize
    const end = start + props.pageSize
    return props.recommendations.slice(start, end)
  } else if (props.showLoadMore) {
    return props.recommendations.slice(0, displayedCount.value)
  }
  return props.recommendations
})

const hasMore = computed(() => {
  if (!props.showLoadMore) return false
  return displayedCount.value < props.recommendations.length
})

// 方法
const isItemFavorite = (item) => {
  return props.favorites.some(fav => fav.id === item.id || fav.title === item.title)
}

const handleRefresh = () => {
  emit('refresh')
}

const handleViewAll = () => {
  emit('view-all')
}

const handleCardClick = (item, index) => {
  emit('card-click', { item, index })
}

const handleFavoriteToggle = (item, isFavorite) => {
  emit('favorite-toggle', { item, isFavorite })
}

const handleCardView = (item) => {
  emit('card-view', item)
}

const handleCardSave = (item) => {
  emit('card-save', item)
}

const changeLayout = (newLayout) => {
  internalLayout.value = newLayout
  emit('layout-change', newLayout)
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    emit('page-change', currentPage.value)
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    emit('page-change', currentPage.value)
  }
}

const handleLoadMore = async () => {
  if (isLoadingMore.value || !hasMore.value) return
  
  isLoadingMore.value = true
  try {
    displayedCount.value += props.loadMoreSize
    emit('load-more', displayedCount.value)
  } finally {
    isLoadingMore.value = false
  }
}

// 监听props变化
watch(() => props.recommendations, () => {
  currentPage.value = 1
  displayedCount.value = props.pageSize
})

watch(() => props.layout, (newLayout) => {
  internalLayout.value = newLayout
})
</script>

<style scoped>
.recommendations-grid {
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
}

/* 头部区域 */
.grid-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
}

.header-left {
  flex: 1;
}

.grid-title {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #e2e8f0;
  margin-bottom: 4px;
}

.grid-subtitle {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 300;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 布局切换 */
.layout-toggle {
  display: flex;
  gap: 4px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 4px;
}

.layout-button {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  transition: all 0.2s ease;
}

.layout-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
}

.layout-button.active {
  background: rgba(139, 92, 246, 0.2);
  color: #8b5cf6;
}

/* 刷新按钮 */
.refresh-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #94a3b8;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.refresh-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-button .refreshing {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 查看更多按钮 */
.view-all-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  color: #c4b5fd;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.view-all-button:hover {
  background: rgba(139, 92, 246, 0.2);
  color: #ffffff;
}

/* 内容区域 */
.grid-content {
  transition: all 0.3s ease;
}

/* 网格布局 */
.grid-content.grid {
  display: grid;
  gap: 24px;
}

.grid-content.grid.cols-1 { grid-template-columns: repeat(1, 1fr); }
.grid-content.grid.cols-2 { grid-template-columns: repeat(2, 1fr); }
.grid-content.grid.cols-3 { grid-template-columns: repeat(3, 1fr); }
.grid-content.grid.cols-4 { grid-template-columns: repeat(4, 1fr); }
.grid-content.grid.cols-5 { grid-template-columns: repeat(5, 1fr); }
.grid-content.grid.cols-6 { grid-template-columns: repeat(6, 1fr); }

/* 列表布局 */
.grid-content.list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.grid-content.list .recommendation-card {
  display: flex;
  flex-direction: row;
}

.grid-content.list .recommendation-card .card-image-container {
  width: 120px;
  height: 120px;
  aspect-ratio: unset;
  border-radius: 12px;
  margin: 12px;
  flex-shrink: 0;
}

.grid-content.list .recommendation-card .card-content {
  flex: 1;
  padding: 16px 16px 16px 0;
}

/* 加载状态 */
.loading-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.loading-text {
  color: #94a3b8;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-text {
  color: #94a3b8;
  font-size: 16px;
  margin: 16px 0;
}

/* 分页控制 */
.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.pagination-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #94a3b8;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.pagination-button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #e2e8f0;
  font-weight: 500;
}

.current-page {
  color: #8b5cf6;
}

.page-separator {
  color: #64748b;
}

.total-pages {
  color: #94a3b8;
}

/* 加载更多 */
.load-more {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.load-more-button {
  padding: 12px 32px;
  border-radius: 8px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  color: #c4b5fd;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.load-more-button:hover:not(:disabled) {
  background: rgba(139, 92, 246, 0.2);
  color: #ffffff;
}

.load-more-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .grid-content.grid.cols-4,
  .grid-content.grid.cols-5,
  .grid-content.grid.cols-6 {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .grid-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .grid-content.grid.cols-3,
  .grid-content.grid.cols-4,
  .grid-content.grid.cols-5,
  .grid-content.grid.cols-6 {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .grid-content.list .recommendation-card {
    flex-direction: column;
  }
  
  .grid-content.list .recommendation-card .card-image-container {
    width: 100%;
    height: auto;
    aspect-ratio: 16/9;
    margin: 0;
    border-radius: 12px 12px 0 0;
  }
  
  .grid-content.list .recommendation-card .card-content {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .grid-content.grid {
    grid-template-columns: 1fr !important;
  }
  
  .pagination-controls {
    flex-direction: column;
    gap: 16px;
  }
  
  .layout-toggle {
    display: none;
  }
}
</style>