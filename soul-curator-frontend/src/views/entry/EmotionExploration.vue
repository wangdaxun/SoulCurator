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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ParticleBackground from '@/components/entry/ParticleBackground.vue'
import ProgressBar from '@/components/entry/ProgressBar.vue'
import QuestionCard from '@/components/entry/QuestionCard.vue'
import SoulPortraitLoading from '@/components/entry/SoulPortraitLoading.vue'
import { useQuizStore } from '@/stores/quiz'
import { SOUL_DATA } from '@/data/questions'
import { useAnalytics } from '@/composables/useAnalytics'
import { useSelection } from '@/composables/useSelection'

const analytics = useAnalytics()
const selection = useSelection()
const quizStore = useQuizStore()
const { questions, setResult, selectedType } = quizStore
const router = useRouter()
const currentStep = ref(1)
const currentQuestionIndex = ref(0)
const totalSteps = ref(questions.length)
const showLoading = ref(false)
const isTransitioning = ref(false)
const selectedOptions = ref([]) // 存储用户选择的选项ID列表
const currentQuestion = computed(() => questions[currentQuestionIndex.value])

onMounted(() => {
  // 记录进入灵魂探索浏览事件
  analytics.recordPageView('EmotionExploration')
  
  // 使用store中的sessionId
  const sessionId = quizStore.sessionId
  console.log('使用会话ID:', sessionId, 'selectedType:', selectedType)
  
  // 初始化选择记录
  if (sessionId && selectedType) {
    console.log('初始化选择记录，sessionId:', sessionId, 'selectedType:', selectedType)
    selection.initSelection(sessionId, selectedType)
  } else {
    console.warn('缺少会话ID或入口类型，无法初始化选择记录')
    // 可以在这里添加错误处理，如跳回入口选择页面
  }
}) 

const handleOptionSelected = async (optionId) => {
  // 构建选择记录
  // 这里的逻辑应该是不管怎么样都应该记录问题
  const selectionRecord = {
    questionId: currentQuestion.value.id,
    optionId: optionId,
    optionText: currentQuestion.value.options.find(opt => opt.id === optionId)?.title || '',
    dimension: currentQuestion.value.dimension || '',
    weight: 1,
    timeSpentSeconds: 5, // 这里可以实际计算花费时间
  }
  
  // 保存到本地状态
  selectedOptions.value.push({
    ...selectionRecord,
    selectedAt: new Date().toISOString() 
  })
  
  // 记录到服务器
  try {
    await selection.recordSelection(selectionRecord)
  } catch (error) {
    console.warn('选择记录失败，但继续流程:', error)
  }
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

const completeExploration = async () => {
  console.log('灵魂探索完成！开始生成画像...')
  console.log('showLoading before:', showLoading.value)

  // 记录完成灵魂探索事件
  analytics.recordEvent({
    eventName: 'EmotionExplorationCompleted',
    eventType: 'completeSoul'
  })

  // 如果已经在过渡中，不重复触发
  if (isTransitioning.value) return

  // 开始过渡动画
  isTransitioning.value = true
  showLoading.value = true

  try {
    
    // 批量同步所有选择到服务器
    await selection.recordSelectionsBatch(
      selectedOptions.value.map((opt, index) => ({
        questionId: opt.questionId,
        optionId: opt.optionId,
        optionText: '', // 这里需要从问题数据中获取
        dimension: '',
        weight: 1,
        timeSpentSeconds: 5,
      }))
    )
    
    console.log('所有选择记录已同步到服务器')
    
  } catch (error) {
    console.warn('选择记录同步失败，但继续画像生成:', error)
  }

  // TODO:调用生成画像的函数，传入用户的答案
  // 这里应该调用后端的画像生成接口，而不是使用静态数据
  setResult(SOUL_DATA) // 暂时使用静态数据
  
  // 检查状态变化
  console.log('showLoading after:', showLoading.value)
  console.log('isTransitioning:', isTransitioning.value)
}

const onLoadingComplete = () => {
  // 加载动画完成后跳转到画像页面
  router.push('/entry/portrait')
  selection.clearStorage() // 清除选择记录
  // 重置状态
  setTimeout(() => {
    showLoading.value = false
    isTransitioning.value = false
  }, 100)
}
</script>