<template>
  <div class="curtain-transition" :class="{ active: isActive }" @animationend="onAnimationEnd">
    <!-- 顶部帷幕 -->
    <div class="curtain top-curtain" :class="{ active: isActive }"></div>

    <!-- 底部帷幕 -->
    <div class="curtain bottom-curtain" :class="{ active: isActive }"></div>

    <!-- 中间开口效果 -->
    <div class="curtain-opening" :class="{ active: isActive }"></div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'

const props = defineProps({
  trigger: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['complete'])

const isActive = ref(false)

// 当trigger变为true时开始动画
watch(() => props.trigger, (newVal) => {
  if (newVal) {
    isActive.value = true
  }
})

const onAnimationEnd = () => {
  if (isActive.value) {
    emit('complete')
    // 重置动画状态
    setTimeout(() => {
      isActive.value = false
    }, 100)
  }
}
</script>

<style scoped>
.curtain-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  pointer-events: none;
}

.curtain {
  position: absolute;
  left: 0;
  width: 100%;
  height: 50%;
  opacity: 0;
  transition:
    transform 1.2s cubic-bezier(0.77, 0, 0.175, 1),
    opacity 0.6s ease-out;
  transform: translateY(0);
  z-index: 1;
}

.top-curtain {
  top: 0;
  background: linear-gradient(
    to bottom,
    rgba(5, 5, 7, 0.3) 0%,
    rgba(5, 5, 7, 0.4) 25%,
    rgba(5, 5, 7, 0.6) 50%,
    rgba(5, 5, 7, 0.4) 75%,
    rgba(5, 5, 7, 0.2) 90%,
    rgba(5, 5, 7, 0) 100%
  );
  backdrop-filter: blur(2px);
}

.bottom-curtain {
  bottom: 0;
  background: linear-gradient(
    to top,
    rgba(5, 5, 7, 0.3) 0%,
    rgba(5, 5, 7, 0.4) 25%,
    rgba(5, 5, 7, 0.6) 50%,
    rgba(5, 5, 7, 0.4) 75%,
    rgba(5, 5, 7, 0.2) 90%,
    rgba(5, 5, 7, 0) 100%
  );
  backdrop-filter: blur(2px);
}

/* 激活时的动画效果 - 先显示再拉开 */
.curtain.active {
  opacity: 1;
  transform: translateY(-100%);
}

.bottom-curtain.active {
  opacity: 1;
  transform: translateY(100%);
}

/* 中间开口效果也只在激活时显示 */
.curtain-opening.active {
  opacity: 1;
  animation: curtain-glow 1.2s ease-in-out;
}

/* 中间开口效果 - 更亮的光晕 */
.curtain-opening {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(
    ellipse at center,
    rgba(147, 51, 234, 0.15) 0%,
    rgba(147, 51, 234, 0.08) 30%,
    rgba(147, 51, 234, 0.02) 60%,
    transparent 100%
  );
  opacity: 0;
  transition: opacity 0.8s ease;
  z-index: 0;
}

.curtain-opening.active {
  opacity: 1;
  animation: curtain-glow 1.2s ease-in-out;
}

@keyframes curtain-glow {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  50% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(1.05);
  }
}

/* 添加一些微妙的粒子效果 */
.curtain::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 20% 80%, rgba(147, 51, 234, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(147, 51, 234, 0.1) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.8s ease;
}

.curtain.active::before {
  opacity: 1;
}
</style>