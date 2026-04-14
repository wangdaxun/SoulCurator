import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useQuizStore = defineStore('quiz', () => {
  // 状态
  const selectedType = ref(null) // 'movie' | 'literature' | 'music' | 'game'
  const currentQuestionIndex = ref(0)
  const answers = ref([]) // 用户的答案列表
  const questions = ref([]) // 问题列表（从后端获取或本地配置）
  const result = ref({}) // 最终推荐结果

  // 计算属性
  const progress = computed(() => {
    if (questions.value.length === 0) return 0
    return Math.round((currentQuestionIndex.value / questions.value.length) * 100)
  })

  const currentQuestion = computed(() => {
    return questions.value[currentQuestionIndex.value] || null
  })

  const isCompleted = computed(() => {
    return currentQuestionIndex.value >= questions.value.length
  })

  // 方法
  function setSelectedType(type) {
    selectedType.value = type
  }

  function setQuestions(questionList) {
    questions.value = questionList
  }

  function answerQuestion(answer) {
    answers.value.push(answer)
    currentQuestionIndex.value++
  }

  function goToPreviousQuestion() {
    if (currentQuestionIndex.value > 0) {
      currentQuestionIndex.value--
      answers.value.pop()
    }
  }

  function setResult(resultData) {
    result.value = resultData
  }

  function reset() {
    selectedType.value = null
    currentQuestionIndex.value = 0
    answers.value = []
    questions.value = []
    result.value = null
  }

  function generatePortrait(answers) {
    // 调用AI生成画像
  }

  return {
    // 状态
    selectedType,
    currentQuestionIndex,
    answers,
    questions,
    result,
    // 计算属性
    progress,
    currentQuestion,
    isCompleted,
    // 方法
    setSelectedType,
    setQuestions,
    answerQuestion,
    goToPreviousQuestion,
    setResult,
    reset,
    generatePortrait
  }
})