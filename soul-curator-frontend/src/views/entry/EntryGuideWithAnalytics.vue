
<template>
  <div class="bg-soul-dark min-h-screen">
    <ParticleBackground />
    <CurtainTransition :trigger="showCurtain" @complete="onCurtainComplete" />
    <div class="desktop-container relative z-10">
      <!-- 头部 -->
      <header class="text-center mb-20">
        <h1 class="soul-title font-serif text-5xl font-light mb-6 tracking-widest text-white/90">
          你的文艺灵魂从何处开始？
        </h1>
        <p class="font-sans text-sm text-white/40 tracking-[0.4em] uppercase">
          WHERE DOES YOUR SOUL JOURNEY BEGIN
        </p>
      </header>

      <!-- 灵魂之门网格 -->
      <div class="grid grid-cols-4 gap-8 w-full max-w-6xl mb-24">
        <SoulGate
          v-for="gate in gates"
          :key="gate.type"
          :color="gate.color"
          :icon="gate.icon"
          :title="gate.title"
          :description="gate.description"
          :selected="selectedType === gate.type"
          :dimmed="selectedType !== null && selectedType !== gate.type"
          @click="handleGateClick(gate.type)"
        />
      </div>
      
      <!-- 调试信息（开发环境显示） -->
      <div v-if="isDevelopment" class="fixed bottom-4 right-4 bg-black/50 text-white p-3 rounded-lg text-xs">
        <div>会话ID: {{ sessionId }}</div>
        <div>分析服务: {{ isAnalyticsReady ? '✅ 正常' : '❌ 异常' }}</div>
        <div>最后事件: {{ lastEvent }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ParticleBackground from '@/components/entry/ParticleBackground.vue'
import SoulGate from '@/components/entry/SoulGate.vue'
import CurtainTransition from '@/components/entry/CurtainTransition.vue'
import { useQuizStore } from '@/stores/quiz'
import { EMOTION_QUESTIONS } from '@/data/questions'
import { useAnalytics, useClickTracker } from '@/composables/useAnalytics'

const quizStore = useQuizStore()
const router = useRouter()
const selectedType = computed(() => quizStore.selectedType)
const showCurtain = ref(false)
const isTransitioning = ref(false)

// 使用分析服务
const { 
  isAnalyticsReady, 
  sessionId, 
  recordPageView, 
  recordClick,
  recordEvent 
} = useAnalytics()

const { createTrackHandler } = useClickTracker()

// 调试信息
const isDevelopment = import.meta.env.DEV
const lastEvent = ref('无')

// 门数据配置
const gates = ref([
  {
    type: 'movie',
    color: 'purple',
    icon: 'clapperboard',
    title: '电影之门',
    description: '在光影编织的梦境里\n寻找另一个自己',
  },
  {
    type: 'literature',
    color: 'blue',
    icon: 'book-open',
    title: '文学之门',
    description: '于纸墨留存的呼吸间\n触碰永恒的真理',
  },
  {
    type: 'music',
    color: 'gold',
    icon: 'music',
    title: '音乐之门',
    description: '由旋律激起的共振中\n释放压抑的灵魂',
  },
  {
    type: 'game',
    color: 'green',
    icon: 'gamepad-2',
    title: '游戏之门',
    description: '在维度交错的交互里\n重塑世界的法则',
  },
])

// 组件挂载时记录页面浏览
onMounted(() => {
  // 记录页面浏览事件
  recordPageView('entry_guide', {
    loadTime: window.performance?.timing ? 
      window.performance.timing.loadEventEnd - window.performance.timing.navigationStart : 0,
    userAgent: navigator.userAgent,
    screenSize: `${window.screen.width}x${window.screen.height}`,
  }).then(() => {
    lastEvent.value = '页面浏览记录成功'
  }).catch(() => {
    lastEvent.value = '页面浏览记录失败'
  })
})

// 处理门的选择/取消
const handleGateClick = (type) => {
  console.log('点击了门:', type)

  // 如果已经在过渡中，不重复触发
  if (isTransitioning.value) return

  // 记录点击事件
  recordClick(`gate_${type}`, {
    gateType: type,
    gateTitle: gates.value.find(g => g.type === type)?.title,
    currentSelection: selectedType.value,
    isSelecting: selectedType.value !== type,
    isDeselecting: selectedType.value === type,
  }).then(() => {
    lastEvent.value = `门点击: ${type}`
  }).catch(() => {
    lastEvent.value = `门点击记录失败: ${type}`
  })

  // 设置选中的门类型
  const newSelection = selectedType.value === type ? null : type
  quizStore.setSelectedType(newSelection)
  
  // 记录选择变化事件
  if (newSelection) {
    recordEvent({
      eventType: 'selection',
      eventName: 'gate_selected',
      eventData: {
        gateType: newSelection,
        gateTitle: gates.value.find(g => g.type === newSelection)?.title,
        action: 'select',
      },
    })
  } else {
    recordEvent({
      eventType: 'selection',
      eventName: 'gate_deselected',
      eventData: {
        gateType: type,
        gateTitle: gates.value.find(g => g.type === type)?.title,
        action: 'deselect',
      },
    })
  }
  
  // TODO.. 调用获取问题接口来自后端获取问题
  quizStore.setQuestions(EMOTION_QUESTIONS)
  
  // 如果选择了门，开始过渡动画
  if (newSelection) {
    // 记录过渡开始事件
    recordEvent({
      eventType: 'transition',
      eventName: 'curtain_transition_started',
      eventData: {
        from: 'entry_guide',
        to: 'emotion_exploration',
        gateType: newSelection,
      },
    }).then(() => {
      lastEvent.value = '过渡开始记录成功'
    }).catch(() => {
      lastEvent.value = '过渡开始记录失败'
    })
    
    isTransitioning.value = true
    showCurtain.value = true
  }
}

// 帷幕动画完成回调
const onCurtainComplete = () => {
  // 记录过渡完成事件
  recordEvent({
    eventType: 'transition',
    eventName: 'curtain_transition_completed',
    eventData: {
      from: 'entry_guide',
      to: 'emotion_exploration',
      gateType: selectedType.value,
      transitionDuration: 1000, // 假设过渡动画1秒
    },
  }).then(() => {
    lastEvent.value = '过渡完成记录成功'
  }).catch(() => {
    lastEvent.value = '过渡完成记录失败'
  })
  
  // 动画完成后跳转到情感探索页面
  router.push('/entry/emotion')
  
  // 重置状态
  setTimeout(() => {
    showCurtain.value = false
    isTransitioning.value = false
  }, 100)
}

// 创建带跟踪的点击处理函数（另一种方式）
const createTrackedClickHandler = (elementName, extraData = {}, originalHandler) => {
  return createTrackHandler(elementName, extraData, originalHandler)
}

// 示例：为其他元素添加跟踪
const handleHelpClick = createTrackedClickHandler('help_button', { location: 'entry_guide' }, () => {
  // 显示帮助信息
  console.log('显示帮助信息')
})

const handleSettingsClick = createTrackedClickHandler('settings_button', { location: 'entry_guide' }, () => {
  // 打开设置
  console.log('打开设置')
})
</script>

<style scoped>
/* 原有的样式保持不变 */
</style>