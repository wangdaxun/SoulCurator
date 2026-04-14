<template>
    <div class="question-card" @click="handleClick">
      <!-- 玻璃态卡片 -->
      <div class="glass-card">
        <!-- 问题序号 -->
        <div class="question-badge">
          问题 {{ questionIndex + 1 }} / {{ totalQuestions }}
        </div>
        
        <!-- 问题标题 -->
        <div class="question-header">
          <h2 class="question-title" ref="titleRef">{{ question?.title }}</h2>
          <div class="divider"></div>
        </div>
        
        <!-- 选项网格 -->
        <div class="options-grid">
          <OptionCard
            v-for="option in question?.options"
            :key="option.id"
            :option="option"
            :is-selected="selectedOptionId === option.id"
            :is-selectable="!selectedOptionId"
            @select="handleOptionSelect"
          />
        </div>
        
        <!-- 状态提示 -->
        <div class="status-area">
          <!-- 未选择时提示 -->
          <div 
            v-if="!selectedOptionId"
            class="selection-prompt"
            role="status"
            aria-live="polite"
          >
            <Icon name="sparkles" :size="16" />
            <span>选择一个选项，继续你的灵魂探索...</span>
          </div>
          
          <!-- 已选择时倒计时 -->
          <div 
            v-else
            class="countdown-area"
            role="status"
            aria-live="assertive"
          >
            <div class="countdown-header">
              <Icon name="check" :size="16" />
              <span>已选择：{{ selectedOptionTitle }}</span>
            </div>
            
            <div class="countdown-timer">
              <div class="countdown-text">
                自动进入下一题：
                <span class="countdown-number">{{ countdown }}</span>
                秒
              </div>
              <div class="countdown-bar">
                <div 
                  class="countdown-progress"
                  :style="{ width: `${countdownProgress}%` }"
                ></div>
              </div>
              
              <!-- 取消按钮（可选） -->
              <button
                v-if="showCancelButton"
                class="cancel-button"
                type="button"
                @click="cancelCountdown"
                aria-label="取消自动进入下一题"
              >
                <Icon name="x" :size="12" />
                取消
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import Icon from "@/components/shared/Icon.vue"
import OptionCard from './OptionCard.vue'
import ShatterEffect from './ShatterEffect.vue'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  questionIndex: {
    type: Number,
    required: true
  },
  totalQuestions: {
    type: Number,
    required: true
  },
  selectedOptionId: {
    type: String,
    default: null
  },
  // 倒计时秒数
  countdownSeconds: {
    type: Number,
    default: 3
  },
  // 是否显示取消按钮
  showCancelButton: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['selectOption', 'countdownComplete', 'countdownCancel'])

const shatterRef = ref(null)
const titleRef = ref(null)

// 倒计时状态
const countdown = ref(props.countdownSeconds)
const countdownTimer = ref(null)
const isCountdownActive = ref(false)

// 计算选中的选项标题
const selectedOptionTitle = computed(() => {
  if (!props.selectedOptionId) return ''
  const option = props.question.options.find(opt => opt.id === props.selectedOptionId)
  return option ? option.title : ''
})

// 计算倒计时进度（用于进度条）
const countdownProgress = computed(() => {
  return ((props.countdownSeconds - countdown.value) / props.countdownSeconds) * 100
})

// 处理选项选择
const handleOptionSelect = (optionId) => {
  if (!props.selectedOptionId) {
    emit('selectOption', optionId)
    startCountdown()
  }
}

// 开始倒计时
const startCountdown = () => {
  // 重置状态
  countdown.value = props.countdownSeconds
  isCountdownActive.value = true
  
  // 清除之前的计时器
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
  
  // 开始新计时器
  countdownTimer.value = setInterval(() => {
    countdown.value--
    
    if (countdown.value <= 0) {
      completeCountdown()
    }
  }, 1000)
}

// 完成倒计时
const completeCountdown = () => {
  clearInterval(countdownTimer.value)
  isCountdownActive.value = false
  emit('countdownComplete')
}

// 取消倒计时
const cancelCountdown = () => {
  clearInterval(countdownTimer.value)
  isCountdownActive.value = false
  countdown.value = props.countdownSeconds
  emit('countdownCancel')
}

// 监听selectedOptionId变化
watch(() => props.selectedOptionId, (newVal, oldVal) => {
  if (newVal && !oldVal) {
    // 从无选择到有选择，开始倒计时
    startCountdown()
  } else if (!newVal && oldVal) {
    // 从有选择到无选择，取消倒计时
    cancelCountdown()
  }
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', fitTitleText)
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})

const handleClick = () => {
  shatterRef.value?.trigger()
}

// 自动调整标题字号以适应行数限制
const fitTitleText = () => {
  if (!titleRef.value) return

  const element = titleRef.value
  const isMobile = window.innerWidth <= 640
  const maxLines = isMobile ? 2 : 1

  // 设置初始字号
  let fontSize = isMobile ? 24 : 36 // 对应 text-2xl 和 text-4xl
  const minFontSize = isMobile ? 16 : 20

  element.style.fontSize = `${fontSize}px`

  // 计算单行高度
  const lineHeight = parseFloat(getComputedStyle(element).lineHeight)
  const maxHeight = lineHeight * maxLines

  // 逐步减小字号直到文字适应
  while (element.scrollHeight > maxHeight && fontSize > minFontSize) {
    fontSize -= 0.5
    element.style.fontSize = `${fontSize}px`
  }
}

// 监听标题变化和窗口大小变化
watch(() => props.question?.title, () => {
  setTimeout(fitTitleText, 0)
})

onMounted(() => {
  fitTitleText()
  window.addEventListener('resize', fitTitleText)
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', fitTitleText)
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style scoped>
.question-card {
  @apply flex-1 flex flex-col items-center justify-center px-4 py-8;
}

/* 玻璃态卡片 */
.glass-card {
  @apply w-[800px] rounded-[20px] p-12 relative;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(147, 51, 234, 0.3);
  box-shadow: 
    0 8px 32px 0 rgba(0, 0, 0, 0.8),
    inset 0 0 15px rgba(147, 51, 234, 0.05);
}

/* 问题序号徽章 */
.question-badge {
  @apply absolute -top-3 -right-3 px-4 py-1.5 bg-brand-purple/20 border border-brand-purple/40 backdrop-blur-xl rounded-full text-[12px] tracking-widest text-brand-accent;
  z-index: 10;
}

/* 问题标题区域 */
.question-header {
  @apply mb-12 text-center;
}

.question-title {
  @apply font-serif text-white/90 tracking-wide mb-4;
  line-height: 1.4;
  white-space: normal;
  overflow: hidden;
}

@media (max-width: 640px) {
  .question-title {
    line-height: 1.4;
  }
}

.divider {
  @apply w-12 h-[1px] bg-brand-purple mx-auto opacity-60;
}

/* 选项网格 */
.options-grid {
  @apply grid grid-cols-2 gap-6;
}

/* 状态区域 */
.status-area {
  @apply mt-8;
}

/* 选择提示 */
.selection-prompt {
  @apply p-4 rounded-lg bg-white/5 border border-white/10 flex items-center justify-center gap-2 text-sm text-white/60;
  animation: fade-in 0.5s ease-out;
}

.selection-prompt i {
  @apply text-brand-accent;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

/* 倒计时区域 */
.countdown-area {
  @apply p-4 rounded-lg bg-brand-purple/10 border border-brand-purple/30;
  animation: slide-up 0.5s ease-out;
}

.countdown-header {
  @apply flex items-center justify-center gap-2 text-sm text-brand-accent mb-3;
}

.countdown-header i {
  @apply text-brand-purple;
}

/* 倒计时器 */
.countdown-timer {
  @apply space-y-2;
}

.countdown-text {
  @apply text-xs text-white/70 text-center;
}

.countdown-number {
  @apply font-bold text-brand-accent;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

/* 倒计时进度条 */
.countdown-bar {
  @apply h-1 bg-white/10 rounded-full overflow-hidden;
}

.countdown-progress {
  @apply h-full bg-brand-purple transition-all duration-1000 ease-linear;
}

/* 取消按钮 */
.cancel-button {
  @apply mt-2 px-3 py-1 text-xs text-white/60 hover:text-white bg-white/5 hover:bg-white/10 border border-white/10 hover:border-white/20 rounded-full transition-all duration-200 flex items-center justify-center gap-1 mx-auto;
}

.cancel-button:hover {
  @apply transform -translate-y-0.5;
}

.cancel-button:active {
  @apply transform scale-95;
}

/* 动画 */
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 900px) {
  .glass-card {
    @apply w-full max-w-[600px] p-8;
  }
  
  .options-grid {
    @apply grid-cols-1 gap-4;
  }
}

@media (max-width: 640px) {
  .glass-card {
    @apply p-6;
  }
  
  .question-badge {
    @apply -top-2 -right-2 px-3 py-1 text-[10px];
  }
  
  .selection-prompt,
  .countdown-area {
    @apply p-3 text-xs;
  }
}
</style>