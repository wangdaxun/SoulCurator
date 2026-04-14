<template>
  <component 
    :is="iconComponent" 
    :size="size" 
    :color="color"
    :stroke-width="strokeWidth"
    :class="customClass"
    :style="customStyle"
  />
</template>

<script setup>
import { defineProps, computed } from 'vue'
import * as LucideIcons from 'lucide-vue-next'

const props = defineProps({
  name: {
    type: String,
    required: true,
    validator: (value) => {
      // 检查是否是有效的Lucide图标名
      // 将kebab-case转换为PascalCase（如：door-open -> DoorOpen）
      const iconName = value
        .split('-')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join('')
      return iconName in LucideIcons
    }
  },
  size: {
    type: [Number, String],
    default: 16
  },
  color: {
    type: String,
    default: 'currentColor'
  },
  strokeWidth: {
    type: [Number, String],
    default: 2
  },
  customClass: {
    type: String,
    default: ''
  },
  customStyle: {
    type: Object,
    default: () => ({})
  }
})

// 动态获取图标组件
const iconComponent = computed(() => {
  // 将kebab-case转换为PascalCase（如：door-open -> DoorOpen, heart -> Heart）
  const iconName = props.name
    .split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join('')
  return LucideIcons[iconName] || LucideIcons['HelpCircle']
})
</script>