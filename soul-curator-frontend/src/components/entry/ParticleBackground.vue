<!-- WebGL 粒子背景组件 - 使用 Three.js 实现高性能渲染 -->
<template>
  <div ref="container" class="particle-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as THREE from 'three'

const container = ref(null)
let scene, camera, renderer, particles, animationId

const PARTICLE_COUNT = 1000 // WebGL 可以轻松处理更多粒子
const PARTICLE_SIZE = 2
const MOVEMENT_SPEED = 0.2

const initThree = () => {
  // 创建场景
  scene = new THREE.Scene()

  // 创建相机
  camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  )
  camera.position.z = 500

  // 创建渲染器
  renderer = new THREE.WebGLRenderer({
    alpha: true,
    antialias: false // 关闭抗锯齿提升性能
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5)) // 限制像素比提升性能
  container.value.appendChild(renderer.domElement)

  // 创建粒子系统
  const geometry = new THREE.BufferGeometry()
  const positions = new Float32Array(PARTICLE_COUNT * 3)
  const velocities = new Float32Array(PARTICLE_COUNT * 3)
  const opacities = new Float32Array(PARTICLE_COUNT)

  for (let i = 0; i < PARTICLE_COUNT; i++) {
    const i3 = i * 3
    positions[i3] = (Math.random() - 0.5) * 1000
    positions[i3 + 1] = (Math.random() - 0.5) * 1000
    positions[i3 + 2] = (Math.random() - 0.5) * 500

    velocities[i3] = (Math.random() - 0.5) * MOVEMENT_SPEED
    velocities[i3 + 1] = (Math.random() - 0.5) * MOVEMENT_SPEED
    velocities[i3 + 2] = (Math.random() - 0.5) * MOVEMENT_SPEED

    opacities[i] = Math.random() * 0.5 + 0.3
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('opacity', new THREE.BufferAttribute(opacities, 1))

  // 存储速度用于动画
  geometry.userData.velocities = velocities

  // 创建材质
  const material = new THREE.PointsMaterial({
    size: PARTICLE_SIZE,
    color: 0xffffff,
    transparent: true,
    opacity: 0.6,
    blending: THREE.AdditiveBlending,
    depthWrite: false
  })

  particles = new THREE.Points(geometry, material)
  scene.add(particles)
}

const animate = () => {
  animationId = requestAnimationFrame(animate)

  const positions = particles.geometry.attributes.position.array
  const velocities = particles.geometry.userData.velocities

  // 更新粒子位置
  for (let i = 0; i < PARTICLE_COUNT; i++) {
    const i3 = i * 3

    positions[i3] += velocities[i3]
    positions[i3 + 1] += velocities[i3 + 1]
    positions[i3 + 2] += velocities[i3 + 2]

    // 边界检测
    if (Math.abs(positions[i3]) > 500) velocities[i3] *= -1
    if (Math.abs(positions[i3 + 1]) > 500) velocities[i3 + 1] *= -1
    if (Math.abs(positions[i3 + 2]) > 250) velocities[i3 + 2] *= -1
  }

  particles.geometry.attributes.position.needsUpdate = true

  // 轻微旋转增加动感
  particles.rotation.y += 0.0002

  renderer.render(scene, camera)
}

const handleResize = () => {
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
}

// 页面可见性优化
const handleVisibilityChange = () => {
  if (document.hidden) {
    if (animationId) cancelAnimationFrame(animationId)
  } else {
    animate()
  }
}

onMounted(() => {
  initThree()
  animate()
  window.addEventListener('resize', handleResize)
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('visibilitychange', handleVisibilityChange)

  // 清理 Three.js 资源
  if (particles) {
    particles.geometry.dispose()
    particles.material.dispose()
  }
  if (renderer) {
    renderer.dispose()
    container.value?.removeChild(renderer.domElement)
  }
})
</script>

<style scoped>
.particle-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>