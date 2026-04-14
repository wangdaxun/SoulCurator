<template>
  <div
    ref="containerRef"
    :class="[
      'shatter-container',
      { 'breaking-animation': isBreaking, vanishing: isVanishing }
    ]"
  >
    <slot />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  shardCount: {
    type: Number,
    default: 24,
  },
  shakeDuration: {
    type: Number,
    default: 200, // ms
  },
  vanishDuration: {
    type: Number,
    default: 300, // ms
  },
})

const emit = defineEmits(['complete'])

const containerRef = ref(null)
const isBreaking = ref(false)
const isVanishing = ref(false)

const createShards = () => {
  const container = containerRef.value
  if (!container) return

  for (let i = 0; i < props.shardCount; i++) {
    const shard = document.createElement('div')
    shard.classList.add('shard')
    const size = Math.random() * 20 + 6
    const left = Math.random() * 100
    const top = Math.random() * 100
    const rotate = Math.random() * 360
    const tx = (Math.random() - 0.5) * 400
    const ty = (Math.random() - 0.5) * 300 - 50
    const bgColor = `rgba(255, 255, 255, ${Math.random() * 0.6 + 0.2})`
    shard.style.cssText = `
      position: absolute;
      left: ${left}%;
      top: ${top}%;
      width: ${size}px;
      height: ${size}px;
      background: ${bgColor};
      transform: rotate(${rotate}deg);
      pointer-events: none;
      z-index: 100;
      border-radius: 2px;
      animation: flyOut 0.5s cubic-bezier(0.23, 1, 0.32, 1) forwards;
      --tx: ${tx}px;
      --ty: ${ty}px;
    `
    container.appendChild(shard)
    shard.addEventListener('animationend', () => shard.remove())
  }
}

const trigger = () => {
  if (isBreaking.value || isVanishing.value) return

  isBreaking.value = true
  createShards()

  setTimeout(() => {
    isBreaking.value = false
    isVanishing.value = true
  }, props.shakeDuration)

  setTimeout(() => {
    isVanishing.value = false
    emit('complete')
  }, props.shakeDuration + props.vanishDuration)
}

defineExpose({ trigger })
</script>

<style scoped>
.shatter-container {
  position: relative;
  overflow: hidden;
}

/* 上下晃动动画 */
@keyframes gateShake {
  0% { transform: translateY(0); }
  25% { transform: translateY(-5px); }
  75% { transform: translateY(5px); }
  100% { transform: translateY(0); }
}

.shatter-container.breaking-animation {
  animation: gateShake 0.2s ease-in-out 0s 2;
  filter: brightness(1.3);
  transform: scale(0.98);
  transition: all 0.1s;
}

/* 渐变消失动画 */
.shatter-container.vanishing {
  animation: vanishOut 0.3s ease-in forwards;
  pointer-events: none;
}

@keyframes vanishOut {
  0% {
    opacity: 1;
    transform: scale(1);
    filter: blur(0);
  }
  100% {
    opacity: 0;
    transform: scale(0.8);
    filter: blur(8px);
    visibility: hidden;
  }
}

/* 碎片飞出动画 */
@keyframes flyOut {
  0% {
    opacity: 1;
    transform: rotate(0deg) scale(1);
    filter: blur(0);
  }
  100% {
    opacity: 0;
    transform: translate(var(--tx, 0), var(--ty, 0)) rotate(180deg) scale(0.5);
    filter: blur(4px);
  }
}
</style>
