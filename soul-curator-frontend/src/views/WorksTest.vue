<template>
  <div class="works-test">
    <h1>Works 接口测试</h1>
    <button @click="fetchWorks" :disabled="loading">{{ loading ? '加载中...' : '获取 Works' }}</button>

    <p v-if="error" class="error">{{ error }}</p>

    <ul v-if="works.length">
      <li v-for="work in works" :key="work.id">
        <pre>{{ JSON.stringify(work, null, 2) }}</pre>
      </li>
    </ul>

    <p v-else-if="!loading && !error">暂无数据</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getWorks } from '@/api/works'

const works = ref([])
const loading = ref(false)
const error = ref('')

async function fetchWorks() {
  loading.value = true
  error.value = ''
  try {
    works.value = await getWorks()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '请求失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.works-test {
  padding: 24px;
  font-family: sans-serif;
}
button {
  padding: 8px 16px;
  cursor: pointer;
}
.error {
  color: red;
}
pre {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow: auto;
}
</style>