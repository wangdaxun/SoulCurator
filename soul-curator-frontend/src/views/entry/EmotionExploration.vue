<template>
  <div class="bg-soul-dark emotion-exploration">
    <ParticleBackground  />
    <!-- 灵魂画像生成加载动画 -->
    <SoulPortraitLoading
      :trigger="showLoading"
      :duration="5000"
      @complete="onLoadingComplete"
    />
    <!-- 进度条 -->
    <ProgressBar :current-step="currentQuestionIndex + 2" />
    <!-- 问题卡片  -->
    <QuestionCard
      :question="currentQuestion"
      :question-index="currentQuestionIndex"
      :total-questions="totalSteps"
      @select-option="handleOptionSelected"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ParticleBackground from '@/components/entry/ParticleBackground.vue'
import ProgressBar from '@/components/entry/ProgressBar.vue'
import QuestionCard from '@/components/entry/QuestionCard.vue'
import SoulPortraitLoading from '@/components/entry/SoulPortraitLoading.vue'
import { useQuizStore } from '@/stores/quiz'
import { SOUL_DATA } from '@/data/questions'
const quizStore = useQuizStore()
const { questions, setResult } = quizStore
const router = useRouter()
const currentStep = ref(1)
const currentQuestionIndex = ref(0)
const totalSteps = ref(questions.length)
const showLoading = ref(false)
const isTransitioning = ref(false)
const currentQuestion = computed(() => questions[currentQuestionIndex.value])

const handleOptionSelected = (optionId) => {
  // 检查是否还有下一题
  if (currentQuestionIndex.value < questions.length - 1) {
    console.log('选择了选项:', optionId + '，进入下一题')
    currentQuestionIndex.value++
    currentStep.value++
  } else {
    // 所有问题完成
    completeExploration()
  }
}

const completeExploration = () => {
  console.log('灵魂探索完成！开始生成画像...')
  console.log('showLoading before:', showLoading.value)

  // 如果已经在过渡中，不重复触发
  if (isTransitioning.value) return

  // 开始过渡动画
  isTransitioning.value = true
  showLoading.value = true

  // TODO:调用生成画像的函数，传入用户的答案
  setResult(SOUL_DATA) // 这里直接使用静态数据，实际应该是根据用户答案生成的结果
  // 检查状态变化
  console.log('showLoading after:', showLoading.value)
  console.log('isTransitioning:', isTransitioning.value)
}

const onLoadingComplete = () => {
  // 加载动画完成后跳转到画像页面
  router.push('/entry/portrait')

  // 重置状态
  setTimeout(() => {
    showLoading.value = false
    isTransitioning.value = false
  }, 100)
}
</script>