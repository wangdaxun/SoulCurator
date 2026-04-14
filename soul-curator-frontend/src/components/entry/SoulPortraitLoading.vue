<template>
  <div class="soul-portrait-loading" :class="{ active: isActive }">
    <!-- 粒子聚拢效果 -->
    <div class="particle-convergence" :class="{ active: isActive }">
      <div class="particle particle-1"></div>
      <div class="particle particle-2"></div>
      <div class="particle particle-3"></div>
      <div class="particle particle-4"></div>
      <div class="particle particle-5"></div>
      <div class="particle particle-6"></div>
      <div class="particle particle-7"></div>
      <div class="particle particle-8"></div>
    </div>

    <!-- 灵魂之光环 -->
    <div class="soul-halo" :class="{ active: isActive }"></div>

    <!-- 中央灵魂轮廓 -->
    <div class="soul-contour" :class="{ active: isActive }">
      <div class="pulse-circle pulse-1"></div>
      <div class="pulse-circle pulse-2"></div>
      <div class="pulse-circle pulse-3"></div>
    </div>

    <!-- 生成文字 -->
    <div class="generating-text" :class="{ active: isActive }">
      <div class="line line-1">灵魂画像正在生成</div>
      <div class="line line-2">与你灵魂的对话...</div>
      <div class="line line-3">聆听内心的回响</div>
    </div>

    <!-- 灵魂能量流 -->
    <div class="energy-flow" :class="{ active: isActive }">
      <div class="flow-line flow-1"></div>
      <div class="flow-line flow-2"></div>
      <div class="flow-line flow-3"></div>
      <div class="flow-line flow-4"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
  duration: {
    type: Number,
    default: 2000 // 默认2秒
  },
  trigger: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['complete'])

const isActive = ref(false)
let timer = null

// 监听trigger变化 - 处理undefined到true的转变
watch(
  () => props.trigger,
  (newVal, oldVal) => {
    console.log('SoulPortraitLoading trigger changed:', oldVal, '->', newVal, 'typeof old:', typeof oldVal, 'typeof new:', typeof newVal)
    console.log('Condition check: newVal === true?', newVal === true, 'oldVal !== true?', oldVal !== true)

    // 如果从任何非true状态变为true，都开始动画
    // 这处理了undefined→true、false→true等情况
    if (newVal === true && oldVal !== true) {
      console.log('Starting loading animation')
      startAnimation()
    } else {
      console.log('Not starting animation - condition not met')
    }
  },
  { immediate: true } // 立即执行一次，确保初始状态正确
)

const startAnimation = () => {
  console.log('SoulPortraitLoading: startAnimation called, isActive:', isActive.value)
  isActive.value = true
  console.log('SoulPortraitLoading: isActive set to:', isActive.value)

  // 设置定时器，动画完成后触发complete事件
  timer = setTimeout(() => {
    console.log('SoulPortraitLoading: animation complete, emitting event')
    emit('complete')
    // 重置状态
    setTimeout(() => {
      isActive.value = false
      console.log('SoulPortraitLoading: isActive reset to false')
    }, 100)
  }, props.duration)
}

// 清理定时器
const cleanup = () => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
}

// 暴露方法供外部调用
defineExpose({
  startAnimation,
  cleanup
})
</script>

<style scoped>
.soul-portrait-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.soul-portrait-loading.active {
  opacity: 1;
  background: rgba(5, 5, 7, 0.35);
  backdrop-filter: blur(3px);
  pointer-events: auto;
}

/* 粒子聚拢效果 */
.particle-convergence {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(147, 51, 234, 0.8);
  border-radius: 50%;
  filter: blur(2px);
  opacity: 0;
  transition: all 2s cubic-bezier(0.16, 1, 0.3, 1);
}

/* 粒子初始位置 */
.particle-1 { top: 20%; left: 20%; }
.particle-2 { top: 80%; left: 20%; }
.particle-3 { top: 20%; left: 80%; }
.particle-4 { top: 80%; left: 80%; }
.particle-5 { top: 50%; left: 10%; }
.particle-6 { top: 50%; left: 90%; }
.particle-7 { top: 10%; left: 50%; }
.particle-8 { top: 90%; left: 50%; }

/* 激活时的聚拢效果 */
.particle-convergence.active .particle {
  opacity: 0.8;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 20px rgba(147, 51, 234, 0.5);
}

/* 光环效果 */
.soul-halo {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 1px solid rgba(147, 51, 234, 0.2);
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: all 1.5s ease-out;
}

.soul-halo.active {
  opacity: 1;
  animation: halo-rotate 8s linear infinite;
}

.soul-halo.active::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 1px solid rgba(147, 51, 234, 0.15);
  transform: translate(-50%, -50%);
  animation: halo-rotate 12s linear infinite reverse;
}

.soul-halo.active::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  border: 1px solid rgba(147, 51, 234, 0.1);
  transform: translate(-50%, -50%);
  animation: halo-rotate 16s linear infinite;
}

@keyframes halo-rotate {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

/* 灵魂轮廓 */
.soul-contour {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120px;
  height: 120px;
  transform: translate(-50%, -50%);
  opacity: 0;
}

.soul-contour.active {
  opacity: 1;
}

.pulse-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  border-radius: 50%;
  border: 2px solid rgba(147, 51, 234, 0.6);
  transform: translate(-50%, -50%);
  animation-duration: 2s;
  animation-iteration-count: infinite;
  animation-timing-function: ease-out;
}

.pulse-1 {
  width: 120px;
  height: 120px;
  animation-name: pulse-1;
  animation-delay: 0s;
}

.pulse-2 {
  width: 150px;
  height: 150px;
  animation-name: pulse-2;
  animation-delay: 0.3s;
}

.pulse-3 {
  width: 180px;
  height: 180px;
  animation-name: pulse-3;
  animation-delay: 0.6s;
}

@keyframes pulse-1 {
  0% { opacity: 1; transform: translate(-50%, -50%) scale(0.8); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(1.2); }
}

@keyframes pulse-2 {
  0% { opacity: 0.8; transform: translate(-50%, -50%) scale(0.7); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(1.3); }
}

@keyframes pulse-3 {
  0% { opacity: 0.6; transform: translate(-50%, -50%) scale(0.6); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(1.4); }
}

/* 生成文字 */
.generating-text {
  position: absolute;
  top: calc(50% + 120px);
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  opacity: 0;
}

.generating-text.active {
  opacity: 1;
}

.line {
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Noto Serif SC', serif;
  font-size: 1.2rem;
  line-height: 1.8;
  letter-spacing: 0.1em;
  text-shadow: 0 0 10px rgba(147, 51, 234, 0.3);
  margin-bottom: 5px;
  opacity: 0;
  transform: translateY(10px);
}

.generating-text.active .line-1 {
  animation: text-appear 0.5s ease-out 0.3s forwards;
}

.generating-text.active .line-2 {
  animation: text-appear 0.5s ease-out 0.8s forwards;
}

.generating-text.active .line-3 {
  animation: text-appear 0.5s ease-out 1.3s forwards;
}

@keyframes text-appear {
  0% { opacity: 0; transform: translateY(10px); }
  100% { opacity: 1; transform: translateY(0); }
}

/* 能量流动 */
.energy-flow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}

.energy-flow.active {
  opacity: 1;
}

.flow-line {
  position: absolute;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(147, 51, 234, 0.3) 20%,
    rgba(147, 51, 234, 0.6) 50%,
    rgba(147, 51, 234, 0.3) 80%,
    transparent 100%
  );
  filter: blur(1px);
  animation-duration: 3s;
  animation-iteration-count: infinite;
  animation-timing-function: ease-in-out;
}

.flow-1 {
  top: 20%;
  left: -100px;
  width: 150px;
  height: 2px;
  animation-name: flow-1;
  animation-delay: 0s;
}

.flow-2 {
  top: 40%;
  right: -100px;
  width: 150px;
  height: 2px;
  animation-name: flow-2;
  animation-delay: 0.5s;
}

.flow-3 {
  top: 60%;
  left: -100px;
  width: 150px;
  height: 2px;
  animation-name: flow-3;
  animation-delay: 1s;
}

.flow-4 {
  top: 80%;
  right: -100px;
  width: 150px;
  height: 2px;
  animation-name: flow-4;
  animation-delay: 1.5s;
}

@keyframes flow-1 {
  0%, 100% { transform: translateX(0) scaleX(0.5); opacity: 0.3; }
  50% { transform: translateX(100px) scaleX(1); opacity: 0.6; }
}

@keyframes flow-2 {
  0%, 100% { transform: translateX(0) scaleX(0.5); opacity: 0.3; }
  50% { transform: translateX(-100px) scaleX(1); opacity: 0.6; }
}

@keyframes flow-3 {
  0%, 100% { transform: translateX(0) scaleX(0.5); opacity: 0.3; }
  50% { transform: translateX(100px) scaleX(1); opacity: 0.6; }
}

@keyframes flow-4 {
  0%, 100% { transform: translateX(0) scaleX(0.5); opacity: 0.3; }
  50% { transform: translateX(-100px) scaleX(1); opacity: 0.6; }
}

/* 添加一些星光效果 */
.soul-portrait-loading::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 30% 30%, rgba(147, 51, 234, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 70% 70%, rgba(147, 51, 234, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 50% 20%, rgba(147, 51, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 20% 80%, rgba(147, 51, 234, 0.03) 0%, transparent 50%);
  opacity: 0.5;
  z-index: -1;
}
</style>