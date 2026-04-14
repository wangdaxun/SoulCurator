<template>
  <div class="bg-soul-dark soul-portrait">
    <particle-background  />
    <div class="content-wrapper">
      <main class="glass-card">
        <!-- Decorative Light Effects -->
        <div class="light-effect light-effect-1"></div>
        <div class="light-effect light-effect-2"></div>
        <soul-portrait-header
          :soul-type="portraitData.soulType"
          :description="portraitData.description"
        />
        <!-- Middle Section: Traits & Quotes -->
        <div class="middle-section">
          <!-- Left: Tags/Traits -->
          <div class="traits-section">
            <h3 class="section-title">
              <span class="title-line"></span> 核心特质
            </h3>
            <div class="tags-container">
              <div 
                v-for="trait in portraitData.traits" 
                :key="trait.name"
                class="trait-tag"
                :style="{ '--tag-color': trait.color || '#8b5cf6' }"
              >
                <Icon :name="trait.icon" :size="16" />
                <span class="tag-text">{{ trait.name }}</span>
              </div>
            </div>
          </div>

          <!-- Right: Quote Card -->
          <div class="quote-section">
            <QuoteCard
              :text="portraitData.quote?.text"
              :author="portraitData.quote?.author"
              theme="gradient"
              :accent-color="'#fbbf24'"
              :interactive="true"
            />
          </div>
        </div>
        <!-- Bottom: Today's Recommendation -->
        <RecommendationsGrid
          :recommendations="portraitData.recommendations"
          title="今日灵魂共鸣"
          title-icon="zap"
          :title-icon-color="'#fbbf24'"
          :columns="3"
          :card-size="'medium'"
          :interactive="true"
          :show-refresh="true"
          :show-refresh-text="true"
          @refresh="handleRefreshRecommendations"
          @card-click="handleRecommendationClick"
        />
      </main>
      <!-- Footer -->
      <footer class="soul-footer">
        <span class="footer-item">
          <Icon name="fingerprint" :size="16" color="#64748b" />
          灵魂特征唯一加密标识: {{ portraitData.metadata?.soulId }}
        </span>
        <span class="footer-dot"></span>
        <span>生成于 {{ formatDate(portraitData.metadata?.generatedAt) }} {{ portraitData.metadata?.node }}</span>
      </footer>
    </div>
  </div>
</template>
<script setup>
import { useQuizStore } from '@/stores/quiz'
import ParticleBackground from '@/components/entry/ParticleBackground.vue'
import SoulPortraitHeader from '@/components/entry/SoulPortraitHeader.vue'
import QuoteCard from '@/components/shared/QuoteCard.vue'
import RecommendationsGrid from '@/components/shared/RecommendationsGrid.vue'
import Icon from "@/components/shared/Icon.vue"
const quizStore = useQuizStore()
const { result: portraitData } = quizStore
// 事件处理方法
const handleRefreshRecommendations = () => {
  console.log('刷新推荐')
  // TODO: 实现刷新推荐逻辑
  // 可以调用API获取新的推荐
}
const handleRecommendationClick = ({ item, index }) => {
  console.log('点击推荐:', item.title, '索引:', index)
  // TODO: 处理推荐点击，如跳转到详情页
}
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
}
</script>
<style scoped>
/* 基础样式 */
.soul-portrait {
  background-color: #020617;
  min-height: 100vh;
  overflow-x: hidden;
  position: relative;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
  color: #e2e8f0;
}

/* 内容包装器 */
.content-wrapper {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 80px 20px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 玻璃态卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  border-radius: 40px;
  width: 100%;
  max-width: 1200px;
  padding: 48px;
  position: relative;
  overflow: hidden;
}

/* 光效装饰 */
.light-effect {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  z-index: 0;
}

.light-effect-1 {
  top: -96px;
  left: -96px;
  width: 384px;
  height: 384px;
  background: rgba(139, 92, 246, 0.2);
}

.light-effect-2 {
  bottom: -96px;
  right: -96px;
  width: 384px;
  height: 384px;
  background: rgba(251, 191, 36, 0.1);
}

/* 中间区域 */
.middle-section {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 32px;
  margin-bottom: 64px;
  position: relative;
  z-index: 1;
}

/* 特质区域 */
.traits-section {
  grid-column: span 7;
}

.section-title {
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-line {
  width: 32px;
  height: 1px;
  background: #334155;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.trait-tag {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  cursor: default;
  position: relative;
  z-index: 1;
}

.trait-tag:hover {
  box-shadow: 0 0 20px rgba(var(--tag-color, 139, 92, 246), 0.4);
  border-color: rgba(var(--tag-color, 139, 92, 246), 0.6);
  transform: translateY(-2px);
}

.tag-text {
  font-weight: 500;
  font-size: 16px;
}

/* 引用区域 */
.quote-section {
  grid-column: span 5;
}

/* 页脚 */
.soul-footer {
  margin-top: 48px;
  color: #64748b;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.footer-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #1e293b;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .middle-section {
    grid-template-columns: 1fr;
    gap: 48px;
  }

  .traits-section,
  .quote-section {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .glass-card {
    padding: 32px 24px;
    border-radius: 32px;
  }

  .content-wrapper {
    padding: 40px 16px;
  }

  .soul-footer {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .footer-dot {
    display: none;
  }

  .tags-container {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .trait-tag {
    padding: 10px 20px;
  }
}
</style>