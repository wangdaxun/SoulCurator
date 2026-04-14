<template>
  <div class="soul-portrait-header">
    <!-- 徽章区域 -->
    <div class="header-badge">
      <Icon :name="badgeIcon" :size="16" color="#fbbf24" />
      <span class="badge-text">{{ badgeText }}</span>
    </div>
    
    <!-- 标题区域 -->
    <h1 class="soul-type-title">{{ soulType }}</h1>
    
    <!-- 描述区域 -->
    <p class="soul-description">{{ description }}</p>
  </div>
</template>

<script setup>
import Icon from "@/components/shared/Icon.vue"
const props = defineProps({
  soulType: {
    type: String,
    required: true,
    default: '深夜思考者'
  },
  description: {
    type: String,
    required: true,
    default: '你的灵魂在静谧的星空下共鸣，于万籁俱寂之时寻找宇宙的逻辑与诗意。'
  },
  badgeText: {
    type: String,
    default: 'Soul Analysis Report'
  },
  badgeIcon: {
    type: String,
    default: 'sparkles'
  }
})
</script>

<style scoped>
.soul-portrait-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 64px;
  text-align: center;
  position: relative;
  z-index: 1;
}

/* 徽章样式 - 完全按照UI图 */
.header-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 24px;
  animation: pulse-slow 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 图标样式已通过Icon组件处理 */

.badge-text {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #fbbf24;
  font-weight: 600;
}

/* 标题样式 - 解决文字显示不全问题 */
.soul-type-title {
  font-size: 72px;
  font-weight: 700;
  letter-spacing: -0.025em;
  background: linear-gradient(to bottom, #ffffff, rgba(255, 255, 255, 0.4));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  line-height: 1.1;
  word-break: break-word; /* 防止长单词溢出 */
  overflow-wrap: break-word; /* 确保长文本换行 */
  max-width: 100%; /* 确保不超过容器宽度 */
}

/* 描述样式 */
.soul-description {
  color: #94a3b8;
  font-size: 18px;
  font-weight: 300;
  max-width: 640px;
  line-height: 1.6;
}

/* 呼吸动画 */
@keyframes pulse-slow {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .soul-type-title {
    font-size: 56px;
  }
}

@media (max-width: 768px) {
  .soul-type-title {
    font-size: 40px;
  }
  
  .soul-description {
    font-size: 16px;
    padding: 0 16px;
  }
}

@media (max-width: 480px) {
  .soul-type-title {
    font-size: 32px;
  }
  
  .header-badge {
    padding: 4px 12px;
  }
  
  .badge-text {
    font-size: 10px;
  }
}
</style>