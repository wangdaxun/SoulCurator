
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
import { useAnalytics } from '@/composables/useAnalytics'
import { getAllSoulGateways, getSoulExpByGatewayType } from '@/api/entry/guide'
import { getOrCreateSession } from '@/api/session'

const analytics = useAnalytics()

const quizStore = useQuizStore()
const router = useRouter()
const selectedType = computed(() => quizStore.selectedType)
const showCurtain = ref(false)
const isTransitioning = ref(false)

// 门数据配置
const gates = ref([])

onMounted(async () => {
  // 记录灵魂之门浏览事件
  analytics.recordPageView('EntryGuide')
  try {
    const response = await getAllSoulGateways()
    if (response.success) {
      gates.value = response.data
    } else {
      console.error('获取灵魂之门数据失败:', response.message)
    }
  } catch (error) {
    console.error('请求灵魂之门数据出错:', error)
  }
})

// 处理门的选择/取消
const handleGateClick = async (type) => {
  console.log('点击了门:', type)

  // 如果已经在过渡中，不重复触发
  if (isTransitioning.value) return

  try {
    // 1. 创建或获取会话
    const sessionId = await getOrCreateSession(type)
    
    // 2. 保存会话ID到store
    quizStore.setSessionId(sessionId)
    
    // 3. 设置选中的门类型
    quizStore.setSelectedType(type)
    
    // 4. 获取问题列表
    const response = await getSoulExpByGatewayType(type) 
    console.log('获取问题接口响应:', response)
    if (response.success) {
      quizStore.setQuestions(response.data?.questions || [])
    } else {
      console.error('获取问题数据失败:', response.message)
    }
    
    // 5. 开始过渡动画
    isTransitioning.value = true
    showCurtain.value = true
    
  } catch (error) {
    console.error('创建会话失败:', error)
    // 可以在这里添加错误处理，如显示提示信息
  }
}

// 帷幕动画完成回调
const onCurtainComplete = () => {
  // 动画完成后跳转到情感探索页面
  router.push('/entry/emotion')
  // 重置状态
  setTimeout(() => {
    showCurtain.value = false
    isTransitioning.value = false
  }, 100)
}
</script>

<style scoped>
</style>