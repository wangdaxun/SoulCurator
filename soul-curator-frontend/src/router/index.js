import { createRouter, createWebHistory } from 'vue-router'
import WorksTest from '@/views/WorksTest.vue'
import EntryGuide from '@/views/entry/EntryGuide.vue'
import EmotionExploration from '@/views/entry/EmotionExploration.vue'
import SoulPortrait from '@/views/entry/SoulPortrait.vue'

const entryRoutes = [
  {
    path: '/entry/guide',
    name: 'EntryGuide',
    component: EntryGuide,
  },
  {
    path: '/entry/emotion',
    name: 'EmotionExploration',
    component: EmotionExploration,
  },
  {
    path: '/entry/portrait',
    name: 'SoulPortrait',
    component: SoulPortrait,
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/entry/guide',
    },
    {
      path: '/works-test',
      name: 'WorksTest',
      component: WorksTest,
    },
    ...entryRoutes
  ],
})

export default router