<template>
  <ShatterEffect ref="shatterRef" @complete="emit('click')">
    <div
      :class="[
        'soul-gate rounded-2xl h-[420px] flex flex-col items-center justify-center p-8 group cursor-pointer transition-all duration-300',
        `gate-${color}`,
        { selected, dimmed }
      ]"
      @click="handleClick"
    >
      <div class="gate-glow" :style="{ backgroundColor: glowColor }"></div>
      <div class="gate-content flex flex-col items-center">
        <div
          class="w-20 h-20 rounded-full flex items-center justify-center mb-8 transition-all duration-700"
          :class="`bg-${color}-soul/10 group-hover:bg-${color}-soul/20`"
        >
          <Icon :name="props.icon" :size="40" />
        </div>
        <h3 class="font-serif text-2xl tracking-widest text-white/80 mb-4">{{ title }}</h3>
        <p class="text-xs text-center text-white/40 leading-relaxed font-light">{{ description }}</p>
      </div>
    </div>
  </ShatterEffect>
</template>

<script setup>
import { computed, ref } from 'vue'
import ShatterEffect from './ShatterEffect.vue'
import Icon from '@/components/shared/Icon.vue'

const props = defineProps({
  color: {
    type: String,
    required: true,
  },
  icon: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  selected: {
    type: Boolean,
    default: false,
  },
  dimmed: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['click'])

const shatterRef = ref(null)

const glowColor = computed(() => {
  const colorMap = {
    purple: '#9333ea',
    blue: '#2563eb',
    gold: '#ca8a04',
    green: '#16a34a',
  }
  return colorMap[props.color] || props.color || '#ffffff'
})

const handleClick = () => {
  shatterRef.value?.trigger()
}
</script>

<style scoped>
.soul-gate {
  position: relative;
  overflow: hidden;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  border: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
}

.gate-content {
  position: relative;
  z-index: 2;
}

.soul-gate::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, transparent 30%, rgba(0, 0, 0, 0.8) 100%);
  z-index: 1;
}

.gate-glow {
  position: absolute;
  width: 150%;
  height: 150%;
  top: 100%;
  left: -25%;
  transition: top 0.8s ease;
  filter: blur(40px);
  opacity: 0.15;
  z-index: 0;
}

.soul-gate:hover .gate-glow,
.soul-gate.selected .gate-glow {
  top: 40%;
  opacity: 0.4;
}

.soul-gate.selected {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.05);
}

.soul-gate.dimmed {
  opacity: 0.3;
  filter: grayscale(0.8) blur(2px);
  pointer-events: auto;
}
</style>

<style>
/* 各门颜色主题光效 */
.gate-purple:hover,
.gate-purple.selected {
  box-shadow: 0 0 50px -10px rgba(147, 51, 234, 0.4);
  border-color: rgba(147, 51, 234, 0.3);
}
.gate-blue:hover,
.gate-blue.selected {
  box-shadow: 0 0 50px -10px rgba(37, 99, 235, 0.4);
  border-color: rgba(37, 99, 235, 0.3);
}
.gate-gold:hover,
.gate-gold.selected {
  box-shadow: 0 0 50px -10px rgba(202, 138, 4, 0.4);
  border-color: rgba(202, 138, 4, 0.3);
}
.gate-green:hover,
.gate-green.selected {
  box-shadow: 0 0 50px -10px rgba(22, 163, 74, 0.4);
  border-color: rgba(22, 163, 74, 0.3);
}
</style>