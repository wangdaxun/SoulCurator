
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ParticleBackground from '@/components/entry/ParticleBackground.vue'
import SoulGate from '@/components/entry/SoulGate.vue'
import CurtainTransition from '@/components/entry/CurtainTransition.vue'
import { useQuizStore } from '@/stores/quiz'
import { EMOTION_QUESTIONS } from '@/data/questions'

const quizStore = useQuizStore()
const router = useRouter()
const selectedType = computed(() => quizStore.selectedType)
const showCurtain = ref(false)
const isTransitioning = ref(false)

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

// 处理门的选择/取消
const handleGateClick = (type) => {
  console.log('点击了门:', type)

  // 如果已经在过渡中，不重复触发
  if (isTransitioning.value) return

  // 设置选中的门类型
  quizStore.setSelectedType(selectedType.value === type ? null : type)
  // TODO.. 调用获取问题接口来自后端获取问题
  quizStore.setQuestions(EMOTION_QUESTIONS)
  // 开始过渡动画
  isTransitioning.value = true
  showCurtain.value = true
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